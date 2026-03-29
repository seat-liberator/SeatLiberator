package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.vacancy.application.exception.VacancyApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration: Vacancy Alert")
public class VacancyRequestTest {

    @Autowired
    VacancyAlertRequester requester;

    @Autowired
    VacancyAlertRequestStore store;

    @Autowired
    VacancyAlertRequestReader reader;

    @Test
    @DisplayName("VacancyAlert 요청 생성 시 정상 저장된다.")
    void VacancyAlert_요청_생성_시_정상_저장된다() {

        //given
        var userId = "user-1";
        var roomId = "room-1";
        var seatId = "seat-1";

        var now = Instant.now();
        var startTime = now.plusSeconds(60);
        var endTime = now.plusSeconds(120);

        var command = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime,
                endTime,
                now
        );

        // when
        var result = requester.request(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getSeatId()).isEqualTo(seatId);
        assertThat(result.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
    }

    @Test
    @DisplayName("VacancyAlert 동일 요청 시 예외 발생")
    void VacancyAlert_동일_요청_시_예외_발생() {

        //given
        //given
        var userId = "user-1";
        var roomId = "room-1";
        var seatId = "seat-1";

        var now = Instant.now();
        var startTime = now.plusSeconds(60);
        var endTime = now.plusSeconds(120);

        var command = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime,
                endTime,
                now
        );

        // 선 저장
        requester.request(command);

        // when & then
        assertThatThrownBy(() -> requester.request(command)).isInstanceOf(VacancyApplicationException.class);
    }

    @Test
    @DisplayName("VacancyAlert 다른 시간 요청 가능")
    void VacancyAlert_다른_시간_요청_가능() {

        //given
        //given
        var userId = "user-1";
        var roomId = "room-1";
        var seatId = "seat-1";

        var now = Instant.now();
        var startTime1 = now.plusSeconds(60);
        var endTime1 = now.plusSeconds(120);
        var startTime2 = now.plusSeconds(70);
        var endTime2 = now.plusSeconds(130);

        var command1 = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime1,
                endTime1,
                now
        );

        var command2 = new VacancyAlertRequestCommand(
                userId,
                roomId,
                seatId,
                startTime2,
                endTime2,
                now
        );

        // when
        var r1 = requester.request(command1);
        var r2 = requester.request(command2);

        // then
        assertThat(r1).isNotNull();
        assertThat(r2).isNotNull();
        assertThat(r1.getUserId()).isEqualTo(userId);
        assertThat(r1.getSeatId()).isEqualTo(seatId);
        assertThat(r1.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
        assertThat(r2.getUserId()).isEqualTo(userId);
        assertThat(r2.getSeatId()).isEqualTo(seatId);
        assertThat(r2.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
    }

    @Test
    @DisplayName("본인이_알람을_취소할_수_있다")
    void 본인이_알람을_취소할_수_있다() {

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
    void 타인은_알람을_취소할_수_없다() {

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
    void 존재하지_않는_알람_취소_시_예외_발생() {

        // when & then
        assertThatThrownBy(() ->
                requester.cancelVacancyAlert(new VacancyAlertCancelCommand("user1", UUID.randomUUID())))
                .isInstanceOf(VacancyApplicationException.class);
    }

    @Test
    @DisplayName("취소된_알람은_다시_신청할_수_있다")
    void 취소된_알람은_다시_신청할_수_있다() {

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
        var now = Instant.now();
        return new VacancyAlertRequestCommand(
                "user1",
                "room1",
                "seat1",
                now.plusSeconds(60),
                now.plusSeconds(120),
                now
        );
    }

}
