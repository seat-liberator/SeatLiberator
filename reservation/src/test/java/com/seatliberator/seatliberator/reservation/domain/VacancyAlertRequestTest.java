package com.seatliberator.seatliberator.reservation.domain;

import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VacancyAlertRequestTest {

    private final Instant now = Instant.now();

    @Test
    @DisplayName("정상 생성")
    void 정상_생성() {

        // given
        Instant start = now.plusSeconds(60);
        Instant end = now.plusSeconds(120);

        // when
        VacancyAlertRequest request = VacancyAlertRequest.of(
                "user1",
                "room1",
                "seat1",
                start,
                end,
                now
        );

        // then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
        assertThat(request.getTargetStartTime()).isEqualTo(start);
        assertThat(request.getTargetEndTime()).isEqualTo(end);
    }

    @Test
    @DisplayName("endTime이_startTime보다_이전이면_예외")
    void endTime이_startTime보다_이전이면_예외() {

        //given
        Instant start = now.plusSeconds(60);
        Instant end = now.minusSeconds(60);

        // when & then
        assertThatThrownBy(() ->
                VacancyAlertRequest.of("u", "r", "s", start, end, now)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("취thㅗ")
    void 취th_ㅗ() {


        // given
        VacancyAlertRequest request = create();

        // when
        request.cancel(now);

        // then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.CANCELLED);
    }

    @Test
    @DisplayName("만료")
    void 만료() {

        // given
        VacancyAlertRequest request = create();

        // when
        request.expire(now);

        //then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.EXPIRED);
    }

    @Test
    @DisplayName("충족")
    void 충족() {

        // given
        VacancyAlertRequest request = create();

        // when
        request.fulfill(now);

        //then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.FULFILLED);
    }

    @Test
    @DisplayName("ACTIVE 상태 아니면 상태 변경 실패")
    void ACTIVE_상태_아니면_상태_변경_실패() {

        // given
        VacancyAlertRequest request = create();
        request.cancel(now);

        // when & then
        assertThatThrownBy(() -> request.expire(now)).isInstanceOf(IllegalStateException.class);
    }

    private VacancyAlertRequest create() {
        return VacancyAlertRequest.of(
                "user1",
                "room1",
                "seat1",
                now.plusSeconds(60),
                now.plusSeconds(120),
                now
        );
    }
}
