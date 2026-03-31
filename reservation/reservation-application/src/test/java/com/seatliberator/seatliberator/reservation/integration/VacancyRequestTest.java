package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.vacancy.application.exception.VacancyApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@TransactionalReservationIntegrationTest
@DisplayName("Integration: Vacancy Alert")
public class VacancyRequestTest {
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Autowired
    VacancyAlertRequester requester;

    @Autowired
    VacancyAlertRequestStore store;

    @Autowired
    VacancyAlertRequestReader reader;

    @Test
    @DisplayName("VacancyAlert 요청 생성 시 정상 저장된다.")
    void save_request_when_vacancy_alert_is_created() {

        //given
        var userId = "user-1";
        var roomId = "room-1";
        var seatId = "seat-1";

        var now = BASE_TIME;
        var startTime = now.plusSeconds(60);
        var endTime = now.plusSeconds(120);

        var command = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime,
                endTime
        );

        // when
        var result = requester.request(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getLocator().roomId()).isEqualTo(roomId);
        assertThat(result.getLocator().seatId()).isEqualTo(seatId);
        assertThat(result.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
    }

    @Test
    @DisplayName("VacancyAlert 동일 요청 시 예외 발생")
    void throw_exception_when_duplicate_vacancy_alert_request_is_created() {

        //given
        //given
        var userId = "user-1";
        var roomId = "room-1";
        var seatId = "seat-1";

        var now = BASE_TIME;
        var startTime = now.plusSeconds(60);
        var endTime = now.plusSeconds(120);

        var command = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime,
                endTime
        );

        // 선 저장
        requester.request(command);

        // when & then
        assertThatThrownBy(() -> requester.request(command)).isInstanceOf(VacancyApplicationException.class);
    }

    @Test
    @DisplayName("VacancyAlert 다른 시간 요청 가능")
    void save_requests_when_vacancy_alert_times_differ() {

        //given
        //given
        var userId = "user-1";
        var roomId = "room-1";
        var seatId = "seat-1";

        var now = BASE_TIME;
        var startTime1 = now.plusSeconds(60);
        var endTime1 = now.plusSeconds(120);
        var startTime2 = now.plusSeconds(70);
        var endTime2 = now.plusSeconds(130);

        var command1 = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime1,
                endTime1
        );

        var command2 = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime2,
                endTime2
        );

        // when
        var r1 = requester.request(command1);
        var r2 = requester.request(command2);

        // then
        assertThat(r1).isNotNull();
        assertThat(r2).isNotNull();
        assertThat(r1.getUserId()).isEqualTo(userId);
        assertThat(r1.getLocator().roomId()).isEqualTo(roomId);
        assertThat(r1.getLocator().seatId()).isEqualTo(seatId);
        assertThat(r1.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
        assertThat(r2.getUserId()).isEqualTo(userId);
        assertThat(r2.getLocator().roomId()).isEqualTo(roomId);
        assertThat(r2.getLocator().seatId()).isEqualTo(seatId);
        assertThat(r2.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
    }

    @Test
    @DisplayName("본인이_알람을_취소할_수_있다")
    void cancel_alert_when_request_user_is_owner() {

        // given
        var command = requestCommand();
        var saved = requester.request(command);

        // when
        requester.cancelVacancyAlert(new VacancyAlertCancelCommand(command.userId(), saved.getId()));

        // then
        var result = reader.findById(saved.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(VacancyAlertStatus.CANCELLED);
    }

    @Test
    @DisplayName("타인은_알람을_취소할_수_없다")
    void throw_exception_when_other_user_cancels_alert() {

        // given
        var command = requestCommand();
        var saved = requester.request(command);

        // when & then
        assertThatThrownBy(() ->
                requester.cancelVacancyAlert(new VacancyAlertCancelCommand("other-user", saved.getId())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지_않는_알람_취소_시_예외_발생")
    void throw_exception_when_canceling_nonexistent_alert() {

        // when & then
        assertThatThrownBy(() ->
                requester.cancelVacancyAlert(new VacancyAlertCancelCommand("user1", UUID.randomUUID())))
                .isInstanceOf(VacancyApplicationException.class);
    }

    @Test
    @DisplayName("취소된_알람은_다시_신청할_수_있다")
    void save_request_again_when_alert_was_cancelled() {

        // given
        var command = requestCommand();
        var saved = requester.request(command);

        requester.cancelVacancyAlert(new VacancyAlertCancelCommand(command.userId(), saved.getId()));

        // when
        var result = requester.request(command);

        // then
        assertThat(result).isNotNull();
    }

    private VacancyAlertRequestCommand requestCommand() {
        var now = BASE_TIME;
        return new VacancyAlertRequestCommand(
                "user1",
                "room1",
                "seat1",
                now.plusSeconds(60),
                now.plusSeconds(120)
        );
    }

}
