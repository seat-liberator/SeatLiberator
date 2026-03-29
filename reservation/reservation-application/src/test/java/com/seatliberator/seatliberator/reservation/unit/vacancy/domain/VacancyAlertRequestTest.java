package com.seatliberator.seatliberator.reservation.unit.vacancy.domain;

import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Vacancy Alert Request")
public class VacancyAlertRequestTest {

    private final Instant now = Instant.now();

    @Test
    @DisplayName("정상 생성")
    void create_active_request_when_arguments_are_valid() {

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
    void throw_exception_when_end_time_is_before_start_time() {

        //given
        Instant start = now.plusSeconds(60);
        Instant end = now.minusSeconds(60);

        // when & then
        assertThatThrownBy(() ->
                VacancyAlertRequest.of("u", "r", "s", start, end, now)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("취소")
    void cancel_request_when_request_is_active() {


        // given
        VacancyAlertRequest request = create();

        // when
        request.cancel(request.getUserId(), now);

        // then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.CANCELLED);
    }

    @Test
    @DisplayName("만료")
    void expire_request_when_request_is_active() {

        // given
        VacancyAlertRequest request = create();

        // when
        request.expire(now);

        //then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.EXPIRED);
    }

    @Test
    @DisplayName("충족")
    void fulfill_request_when_request_is_active() {

        // given
        VacancyAlertRequest request = create();

        // when
        request.fulfill(now);

        //then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.FULFILLED);
    }

    @Test
    @DisplayName("ACTIVE 상태 아니면 상태 변경 실패")
    void throw_exception_when_transitioning_non_active_request() {

        // given
        VacancyAlertRequest request = create();
        request.cancel(request.getUserId(), now);

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
