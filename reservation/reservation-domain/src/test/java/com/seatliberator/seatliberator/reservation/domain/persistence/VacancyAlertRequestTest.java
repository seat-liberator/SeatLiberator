package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain: Vacancy Alert Request")
public class VacancyAlertRequestTest {

    private final Instant now = Instant.now();

    @Test
    @DisplayName("정상 생성")
    void create_active_request_when_arguments_are_valid() {

        // given
        Instant start = now.plusSeconds(60);
        Instant end = now.plusSeconds(120);
        var locator = SimpleSeatLocator.from("room1", "seat1");
        var range = SimpleTimeRange.from(start, end);

        // when
        VacancyAlertRequest request = VacancyAlertRequest.create("user1", locator, range, now);

        // then
        assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
        assertThat(request.getRange().startAt()).isEqualTo(start);
        assertThat(request.getRange().endAt()).isEqualTo(end);
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
        Assertions.assertThatThrownBy(() -> request.expire(now)).isInstanceOf(IllegalStateException.class);
    }

    private VacancyAlertRequest create() {
        var locator = SimpleSeatLocator.from("room1", "seat1");
        var range = SimpleTimeRange.from(now.plusSeconds(60), now.plusSeconds(120));
        return VacancyAlertRequest.create("user1", locator, range, now);
    }
}
