package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.VacancyAlertRequestCreateCommandBuilder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.service.SeatCommandService;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.RequestVacancyAlertUseCase;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.VacancyAlertRequestApplicationFixture.createVacancyAlertRequestCancelCommand;
import static com.seatliberator.seatliberator.reservation.VacancyAlertRequestApplicationFixture.createVacancyRequestCreateCommand;
import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@TransactionalReservationIntegrationTest
@DisplayName("Integration: Vacancy Alert")
public class VacancyRequestTest {
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Autowired
    RequestVacancyAlertUseCase requester;

    @Autowired
    CreateReservationUseCase createReservationUseCase;

    @Autowired
    SeatCommandService seatService;

    @Autowired
    VacancyAlertRequestStore store;

    @BeforeEach
    void run() {
        var locator = createLocator();
        var range = createRange();

        seatService.create(new CreateSeatCommand(locator.roomId(), locator.seatId()));
        createReservationUseCase.create(new CreateReservationCommand(INITIAL_USER_ID, locator.roomId(), locator.seatId(), range.startAt(), range.endAt()));
    }

    @Test
    @DisplayName("VacancyAlert 요청 생성 시 정상 저장된다.")
    void save_request_when_vacancy_alert_is_created() {
        var command = createVacancyRequestCreateCommand();

        var userId = command.userId();
        var roomId = command.roomId();
        var seatId = command.seatId();
        var startAt = command.startTime();
        var endAt = command.endTime();

        // when
        var result = requester.request(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getLocator().roomId()).isEqualTo(roomId);
        assertThat(result.getLocator().seatId()).isEqualTo(seatId);
        assertThat(result.getRange().startAt()).isEqualTo(startAt);
        assertThat(result.getRange().endAt()).isEqualTo(endAt);
        assertThat(result.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.ACTIVE);
    }

    @Test
    @DisplayName("VacancyAlert 동일 요청 시 예외 발생")
    void throw_exception_when_duplicate_vacancy_alert_request_is_created() {
        var command = createVacancyRequestCreateCommand();

        // 선 저장
        requester.request(command);

        // when & then
        assertThatThrownBy(() -> requester.request(command)).isInstanceOf(ReservationApplicationException.class);
    }

    @Test
    @DisplayName("VacancyAlert 다른 시간 요청 가능")
    void save_requests_when_vacancy_alert_times_differ() {

        //given
        var userId = INITIAL_USER_ID;
        var locator = createLocator();

        var now = BASE_TIME;
        var startTime1 = now.plusSeconds(60);
        var startTime2 = now.plusSeconds(70);

        var range1 = createRange(startTime1);
        var range2 = createRange(startTime2);

        var baseCommandBuilder = new VacancyAlertRequestCreateCommandBuilder()
                .userId(userId)
                .locator(locator);

        var command1 = baseCommandBuilder.copy().range(range1).build();
        var command2 = baseCommandBuilder.copy().range(range2).build();

        // when
        var r1 = requester.request(command1);
        var r2 = requester.request(command2);

        // then
        assertThat(r1).isNotNull();
        assertThat(r2).isNotNull();
        assertThat(r1.getUserId()).isEqualTo(userId);
        assertThat(r1.getLocator().roomId()).isEqualTo(locator.roomId());
        assertThat(r1.getLocator().seatId()).isEqualTo(locator.seatId());
        assertThat(r1.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.ACTIVE);

        assertThat(r2.getUserId()).isEqualTo(userId);
        assertThat(r2.getLocator().roomId()).isEqualTo(locator.roomId());
        assertThat(r2.getLocator().seatId()).isEqualTo(locator.seatId());
        assertThat(r2.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.ACTIVE);
    }

    @Test
    @DisplayName("본인이_알람을_취소할_수_있다")
    void cancel_alert_when_request_user_is_owner() {

        // given
        var command = createVacancyRequestCreateCommand();
        var saved = requester.request(command);

        // when
        var cancelCommand = createVacancyAlertRequestCancelCommand(command.userId(), saved.getId());
        requester.cancel(cancelCommand);

        // then
        var result = store.findById(saved.getId());
        assertThat(result.isPresent()).isTrue();
        var waitlist = result.get();
        var state = waitlist.getState();
        assertThat(state.getStatus()).isEqualTo(VacancyAlertRequestStatus.CANCELLED);
    }

    @Test
    @DisplayName("타인은_알람을_취소할_수_없다")
    void throw_exception_when_other_user_cancels_alert() {

        // given
        var command = createVacancyRequestCreateCommand();
        var saved = requester.request(command);

        // when & then
        var cancelCommand = createVacancyAlertRequestCancelCommand("other-" + command.userId(), saved.getId());
        assertThatThrownBy(() -> requester.cancel(cancelCommand))
                .isInstanceOf(ReservationApplicationException.class)
                .hasMessage("알람을 취소할 권한이 없습니다.");
    }

    @Test
    @DisplayName("존재하지_않는_알람_취소_시_예외_발생")
    void throw_exception_when_canceling_nonexistent_alert() {

        // when & then
        var cancelCommand = createVacancyAlertRequestCancelCommand();
        assertThatThrownBy(() -> requester.cancel(cancelCommand))
                .isInstanceOf(ReservationApplicationException.class);
    }

    @Test
    @DisplayName("취소된_알람은_다시_신청할_수_있다")
    void save_request_again_when_alert_was_cancelled() {

        // given
        var command = createVacancyRequestCreateCommand();
        var saved = requester.request(command);

        var cancelCommand = createVacancyAlertRequestCancelCommand(command.userId(), saved.getId());
        requester.cancel(cancelCommand);

        // when
        var result = requester.request(command);

        // then
        assertThat(result).isNotNull();
    }
}
