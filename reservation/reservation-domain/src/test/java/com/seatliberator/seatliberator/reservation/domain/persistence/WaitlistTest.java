package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.domain.WaitlistResolution;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixture.createRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Vacancy Alert Request")
public class WaitlistTest {
    @Nested
    @DisplayName("Constructor")
    class Constructor {
        @Test
        @DisplayName("Notify only request 생성")
        void create_notify_only_request() {

            // given
            var locator = createLocator();
            var range = createRange();
            var requestedAt = fixedClock.instant();

            // when
            Waitlist request = Waitlist.notifyOnly(
                    INITIAL_USER_ID,
                    locator,
                    range,
                    requestedAt
            );

            // then
            assertThat(request.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
            assertThat(request.getBehavior()).isEqualTo(WaitlistBehavior.NOTIFY_ONLY);
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

            assertThatThrownBy(() -> Waitlist.notifyOnly(
                    INITIAL_USER_ID,
                    locator,
                    range,
                    requestedAtExactSameAsStartAt
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("requestedAt is must be before startAt");

            assertThatThrownBy(() -> Waitlist.autoClaim(
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
    @DisplayName("Transition")
    class Transition {

        @Test
        @DisplayName("ACTIVE 상태 아니면 상태 변경 실패")
        void throw_exception_when_transitioning_non_active_request() {

            // given
            Waitlist request = createRequest();
            var now = fixedClock.instant();
            request.cancel(now);

            // when & then
            assertThatThrownBy(() -> request.expire(now)).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> request.complete(now)).isInstanceOf(IllegalStateException.class);
        }

        @Nested
        @DisplayName("Cancel")
        class Cancel {
            @Test
            @DisplayName("requestedAt 이후 취소 처리 가능")
            void can_cancel_after_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt();

                r.cancel(now);

                var state = r.getState();
                assertThat(state.getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
                assertThat(state.getCancelledAt()).isEqualTo(now);
                assertThat(state.getExpiredAt()).isNull();
                assertThat(state.getFailedAt()).isNull();
                assertThat(state.getCompletedAt()).isNull();
            }

            @Test
            @DisplayName("requestedAt 보다 이른 시각에 취소 처리하면 예외 발생")
            void throw_exception_when_cancelled_at_is_before_than_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt().minusSeconds(1);

                assertThatThrownBy(() -> r.cancel(now))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("cancelledAt must not be before requestedAt");
            }
        }

        @Nested
        @DisplayName("Expire")
        class Expire {
            @Test
            @DisplayName("requestedAt 이후 만료 처리 가능")
            void can_expire_after_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt();

                r.expire(now);

                var state = r.getState();
                assertThat(state.getStatus()).isEqualTo(WaitlistStatus.EXPIRED);
                assertThat(state.getCancelledAt()).isNull();
                assertThat(state.getExpiredAt()).isEqualTo(now);
                assertThat(state.getFailedAt()).isNull();
                assertThat(state.getCompletedAt()).isNull();
            }

            @Test
            @DisplayName("requestedAt 보다 이른 시각에 만료 처리하면 예외 발생")
            void throw_exception_when_cancelled_at_is_before_than_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt().minusSeconds(1);

                assertThatThrownBy(() -> r.expire(now))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("expiredAt must not be before requestedAt");
            }
        }

        @Nested
        @DisplayName("Fail")
        class Fail {
            @Test
            @DisplayName("requestAt 이후 실패 처리 가능")
            void can_fail_after_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt();

                r.fail(now);

                var state = r.getState();
                assertThat(state.getStatus()).isEqualTo(WaitlistStatus.FAILED);
                assertThat(state.getCancelledAt()).isNull();
                assertThat(state.getExpiredAt()).isNull();
                assertThat(state.getFailedAt()).isEqualTo(now);
                assertThat(state.getCompletedAt()).isNull();
            }

            @Test
            @DisplayName("requestedAt 보다 이른 시각에 실패 처리하면 예외 발생")
            void throw_exception_when_failed_at_is_before_than_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt().minusSeconds(1);

                assertThatThrownBy(() -> r.fail(now))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("failedAt must not be before requestedAt");
            }
        }

        @Nested
        @DisplayName("Complete")
        class Complete {
            @Test
            @DisplayName("requestedAt 이후 완료 처리 가능")
            void can_complete_after_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt();

                r.complete(now);

                var state = r.getState();
                assertThat(state.getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
                assertThat(state.getCancelledAt()).isNull();
                assertThat(state.getExpiredAt()).isNull();
                assertThat(state.getFailedAt()).isNull();
                assertThat(state.getCompletedAt()).isEqualTo(now);
            }

            @Test
            @DisplayName("requestedAt 보다 이른 시각에 완료 처리하면 예외 발생")
            void throw_exception_when_completed_at_is_before_than_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt().minusSeconds(1);

                assertThatThrownBy(() -> r.complete(now))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("completedAt must not be before requestedAt");
            }

            @Test
            @DisplayName("Action type이 Notify only인 request는 complete 시 Resolution이 NOTIFIED로 전이")
            void transition_resolution_to_notified_when_action_type_is_notify_only() {
                var locator = createLocator();
                var range = createRange();
                var requestedAt = range.startAt().minusSeconds(1);
                var now = requestedAt.minusSeconds(1);
                var r = Waitlist.notifyOnly(
                        INITIAL_USER_ID,
                        locator,
                        range,
                        now
                );

                r.complete(now);

                var state = r.getState();
                assertThat(state.getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
                assertThat(state.getResolution()).isEqualTo(WaitlistResolution.NOTIFIED);
            }

            @Test
            @DisplayName("Action type이 Auto claim인 request는 complete 시 Resolution이 CLAIMED로 전이")
            void transition_resolution_to_claimed_when_action_type_is_auto_claim() {
                var locator = createLocator();
                var range = createRange();
                var requestedAt = range.startAt().minusSeconds(1);
                var now = requestedAt.minusSeconds(1);
                var r = Waitlist.autoClaim(
                        INITIAL_USER_ID,
                        locator,
                        range,
                        now
                );

                r.complete(now);

                var state = r.getState();
                assertThat(state.getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
                assertThat(state.getResolution()).isEqualTo(WaitlistResolution.CLAIMED);
            }

            @Test
            @DisplayName("requestedAt 보다 이른 시각에 만료 처리하면 예외 발생")
            void throw_exception_when_cancelled_at_is_before_than_requested_at() {
                var r = createRequest();
                var now = r.getState().getRequestedAt().minusSeconds(1);

                assertThatThrownBy(() -> r.expire(now))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("expiredAt must not be before requestedAt");
            }
        }
    }
}
