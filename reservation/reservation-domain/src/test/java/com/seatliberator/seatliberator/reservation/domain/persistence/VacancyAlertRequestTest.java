package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Vacancy Alert Request")
public class VacancyAlertRequestTest {
    @Nested
    @DisplayName("생성자")
    class Constructor {
        @Test
        @DisplayName("정상 생성")
        void create_active_request_when_arguments_are_valid() {

            // given
            var locator = createLocator();
            var range = createRange();
            var requestedAt = range.startAt().minusSeconds(1);

            // when
            VacancyAlertRequest request = VacancyAlertRequest.create(
                    INITIAL_USER_ID,
                    locator,
                    range,
                    requestedAt
            );

            // then
            assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
            assertThat(request.getRange().startAt()).isEqualTo(range.startAt());
            assertThat(request.getRange().endAt()).isEqualTo(range.endAt());
        }

        @Test
        @DisplayName("requestedAt이 TimeRange.startAt보다 과거가 아니면 예외")
        void throw_exception_when_requested_at_is_not_past_than_startAt() {
            // given
            var locator = createLocator();
            var range = createRange();

            // when requestedAt == range.startAt is not the past.
            var requestedAtExactSameAsStartAt = range.startAt();
            var requestedAtFutureThanStartAt = range.startAt().plusSeconds(1);

            assertThatThrownBy(() -> VacancyAlertRequest.create(
                    INITIAL_USER_ID,
                    locator,
                    range,
                    requestedAtExactSameAsStartAt
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("requestedAt is must be before startAt");

            assertThatThrownBy(() -> VacancyAlertRequest.create(
                    INITIAL_USER_ID,
                    locator,
                    range,
                    requestedAtFutureThanStartAt
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("requestedAt is must be before startAt");
        }
    }

    @Nested
    @DisplayName("상태 전이")
    class Transition {

        @Test
        @DisplayName("취소")
        void cancel_request_when_request_is_active() {
            // given
            VacancyAlertRequest request = createAlert();
            var now = fixedClock.instant();

            // when
            request.cancel(request.getUserId(), now);

            // then
            assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.CANCELLED);
        }

        @Test
        @DisplayName("만료")
        void expire_request_when_request_is_active() {

            // given
            VacancyAlertRequest request = createAlert();
            var now = fixedClock.instant();

            // when
            request.expire(now);

            //then
            assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.EXPIRED);
        }

        @Test
        @DisplayName("충족")
        void fulfill_request_when_request_is_active() {

            // given
            VacancyAlertRequest request = createAlert();
            var now = fixedClock.instant();

            // when
            request.fulfill(now);

            //then
            assertThat(request.getStatus()).isEqualTo(VacancyAlertStatus.FULFILLED);
        }

        @Test
        @DisplayName("ACTIVE 상태 아니면 상태 변경 실패")
        void throw_exception_when_transitioning_non_active_request() {

            // given
            VacancyAlertRequest request = createAlert();
            var now = fixedClock.instant();
            request.cancel(request.getUserId(), now);

            // when & then
            assertThatThrownBy(() -> request.expire(now)).isInstanceOf(IllegalStateException.class);
        }
    }
}
