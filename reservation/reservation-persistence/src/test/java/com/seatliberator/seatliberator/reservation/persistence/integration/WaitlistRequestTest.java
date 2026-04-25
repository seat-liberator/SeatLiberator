package com.seatliberator.seatliberator.reservation.persistence.integration;

import com.seatliberator.seatliberator.reservation.WaitlistCreateCommandBuilder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.service.SeatCommandService;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.CancelWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.CreateWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.WaitlistApplicationFixture.createWaitlistCancelCommand;
import static com.seatliberator.seatliberator.reservation.WaitlistApplicationFixture.createWaitlistCreateCommand;
import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@TransactionalReservationPersistenceIntegrationTest
@DisplayName("Integration: Waitlist")
public class WaitlistRequestTest {
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Autowired
    CreateRoomUseCase createRoomUseCase;

    @Autowired
    CreateWaitlistUseCase createWaitlistUseCase;

    @Autowired
    CancelWaitlistUseCase cancelWaitlistUseCase;

    @Autowired
    CreateReservationUseCase createReservationUseCase;

    @Autowired
    SeatCommandService seatService;

    @Autowired
    WaitlistStore store;

    @BeforeEach
    void run() {
        var locator = createLocator();
        var range = createRange();

        createRoomUseCase.create(new CreateRoomCommand(locator.roomId()));
        seatService.create(new CreateSeatCommand(locator.roomId(), locator.seatId()));
        createReservationUseCase.create(new CreateReservationCommand(INITIAL_USER_ID, locator.roomId(), locator.seatId(), range.startAt(), range.endAt()));
    }

    @Test
    @DisplayName("Waitlist 요청 생성 시 정상 저장된다.")
    void save_request_when_waitlist_is_created() {
        var command = createWaitlistCreateCommand();

        var userId = command.userId();
        var roomId = command.roomId();
        var seatId = command.seatId();
        var startAt = command.startTime();
        var endAt = command.endTime();

        // when
        var result = createWaitlistUseCase.create(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getLocator().roomId()).isEqualTo(roomId);
        assertThat(result.getLocator().seatId()).isEqualTo(seatId);
        assertThat(result.getRange().startAt()).isEqualTo(startAt);
        assertThat(result.getRange().endAt()).isEqualTo(endAt);
        assertThat(result.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
    }

    @Test
    @DisplayName("Waitlist 동일 요청 시 예외 발생")
    void throw_exception_when_duplicate_waitlist_request_is_created() {
        var command = createWaitlistCreateCommand();

        // 선 저장
        createWaitlistUseCase.create(command);

        // when & then
        assertThatThrownBy(() -> createWaitlistUseCase.create(command)).isInstanceOf(ReservationApplicationException.class);
    }

    @Test
    @DisplayName("Waitlist 다른 시간 요청 가능")
    void save_requests_when_waitlist_times_differ() {

        //given
        var userId = INITIAL_USER_ID;
        var locator = createLocator();

        var now = BASE_TIME;
        var startTime1 = now.plusSeconds(60);
        var startTime2 = now.plusSeconds(70);

        var range1 = createRange(startTime1);
        var range2 = createRange(startTime2);

        var baseCommandBuilder = new WaitlistCreateCommandBuilder()
                .userId(userId)
                .locator(locator);

        var command1 = baseCommandBuilder.copy().range(range1).build();
        var command2 = baseCommandBuilder.copy().range(range2).build();

        // when
        var r1 = createWaitlistUseCase.create(command1);
        var r2 = createWaitlistUseCase.create(command2);

        // then
        assertThat(r1).isNotNull();
        assertThat(r2).isNotNull();
        assertThat(r1.getUserId()).isEqualTo(userId);
        assertThat(r1.getLocator().roomId()).isEqualTo(locator.roomId());
        assertThat(r1.getLocator().seatId()).isEqualTo(locator.seatId());
        assertThat(r1.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);

        assertThat(r2.getUserId()).isEqualTo(userId);
        assertThat(r2.getLocator().roomId()).isEqualTo(locator.roomId());
        assertThat(r2.getLocator().seatId()).isEqualTo(locator.seatId());
        assertThat(r2.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
    }

    @Test
    @DisplayName("본인이_대기열_요청을_취소할_수_있다")
    void cancel_waitlist_when_request_user_is_owner() {

        // given
        var command = createWaitlistCreateCommand();
        var saved = createWaitlistUseCase.create(command);

        // when
        var cancelCommand = createWaitlistCancelCommand(command.userId(), saved.getId());
        cancelWaitlistUseCase.cancel(cancelCommand);

        // then
        var result = store.findById(saved.getId());
        assertThat(result.isPresent()).isTrue();
        var waitlist = result.get();
        var state = waitlist.getState();
        assertThat(state.getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
    }

    @Test
    @DisplayName("타인은_대기열_요청을_취소할_수_없다")
    void throw_exception_when_other_user_cancels_waitlist() {

        // given
        var command = createWaitlistCreateCommand();
        var saved = createWaitlistUseCase.create(command);

        // when & then
        var cancelCommand = createWaitlistCancelCommand("other-" + command.userId(), saved.getId());
        assertThatThrownBy(() -> cancelWaitlistUseCase.cancel(cancelCommand))
                .isInstanceOf(ReservationApplicationException.class)
                .hasMessage("대기열 요청을 취소할 권한이 없습니다.");
    }

    @Test
    @DisplayName("존재하지_않는_대기열_요청_취소_시_예외_발생")
    void throw_exception_when_canceling_nonexistent_waitlist() {

        // when & then
        var cancelCommand = createWaitlistCancelCommand();
        assertThatThrownBy(() -> cancelWaitlistUseCase.cancel(cancelCommand))
                .isInstanceOf(ReservationApplicationException.class);
    }

    @Test
    @DisplayName("취소된_대기열_요청은_다시_신청할_수_있다")
    void save_request_again_when_waitlist_was_cancelled() {

        // given
        var command = createWaitlistCreateCommand();
        var saved = createWaitlistUseCase.create(command);

        var cancelCommand = createWaitlistCancelCommand(command.userId(), saved.getId());
        cancelWaitlistUseCase.cancel(cancelCommand);

        // when
        var result = createWaitlistUseCase.create(command);

        // then
        assertThat(result).isNotNull();
    }
}
