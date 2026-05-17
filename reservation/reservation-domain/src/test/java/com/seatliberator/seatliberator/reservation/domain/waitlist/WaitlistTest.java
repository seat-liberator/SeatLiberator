package com.seatliberator.seatliberator.reservation.domain.waitlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Waitlist")
public class WaitlistTest {

    @Test
    @DisplayName("알림 전용 대기열은 생성되면 ACTIVE 상태와 요청 시각을 가진다")
    void create_notify_only_waitlist() {
        var requestedAt = fixedClock.instant();

        var waitlist = Waitlist.notifyOnly(
                INITIAL_USER_ID,
                INITIAL_SLOT_IDS,
                INITIAL_OCCUPANCY_DATE,
                requestedAt
        );

        assertThat(waitlist.getUserId()).isEqualTo(INITIAL_USER_ID);
        assertThat(waitlist.getSlotIds()).isEqualTo(INITIAL_SLOT_IDS);
        assertThat(waitlist.getOccupancyDate()).isEqualTo(INITIAL_OCCUPANCY_DATE);
        assertThat(waitlist.getBehavior()).isEqualTo(WaitlistBehavior.NOTIFY_ONLY);
        assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
        assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.PENDING);
        assertThat(waitlist.getState().getRequestedAt()).isEqualTo(requestedAt);
        assertThat(waitlist.getState().getCancelledAt()).isNull();
        assertThat(waitlist.getState().getExpiredAt()).isNull();
        assertThat(waitlist.getState().getFailedAt()).isNull();
        assertThat(waitlist.getState().getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("자동 점유 대기열은 생성되면 AUTO_CLAIM 처리 방식을 가진다")
    void create_auto_claim_waitlist() {
        var requestedAt = fixedClock.instant();

        var waitlist = Waitlist.autoClaim(
                INITIAL_USER_ID,
                INITIAL_SLOT_IDS,
                INITIAL_OCCUPANCY_DATE,
                requestedAt
        );

        assertThat(waitlist.getUserId()).isEqualTo(INITIAL_USER_ID);
        assertThat(waitlist.getSlotIds()).isEqualTo(INITIAL_SLOT_IDS);
        assertThat(waitlist.getOccupancyDate()).isEqualTo(INITIAL_OCCUPANCY_DATE);
        assertThat(waitlist.getBehavior()).isEqualTo(WaitlistBehavior.AUTO_CLAIM);
        assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
        assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.PENDING);
        assertThat(waitlist.getState().getRequestedAt()).isEqualTo(requestedAt);
        assertThat(waitlist.getState().getCancelledAt()).isNull();
        assertThat(waitlist.getState().getExpiredAt()).isNull();
        assertThat(waitlist.getState().getFailedAt()).isNull();
        assertThat(waitlist.getState().getCompletedAt()).isNull();
    }

    @Nested
    @DisplayName("Transition from ACTIVE")
    class TransitionFromActive {
        @Test
        @DisplayName("활성 대기열은 취소 처리할 수 있다")
        void cancel_active_waitlist() {
            var waitlist = createWaitlist();
            var cancelledAt = fixedClock.instant().plusSeconds(1);

            waitlist.cancel(cancelledAt);

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
            assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.PENDING);
            assertThat(waitlist.getState().getCancelledAt()).isEqualTo(cancelledAt);
            assertThat(waitlist.getState().getExpiredAt()).isNull();
            assertThat(waitlist.getState().getFailedAt()).isNull();
            assertThat(waitlist.getState().getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("활성 대기열은 만료 처리할 수 있다")
        void expire_active_waitlist() {
            var waitlist = createWaitlist();
            var expiredAt = fixedClock.instant().plusSeconds(1);

            waitlist.expire(expiredAt);

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.EXPIRED);
            assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.PENDING);
            assertThat(waitlist.getState().getExpiredAt()).isEqualTo(expiredAt);
            assertThat(waitlist.getState().getCancelledAt()).isNull();
            assertThat(waitlist.getState().getFailedAt()).isNull();
            assertThat(waitlist.getState().getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("활성 대기열은 실패 처리할 수 있다")
        void fail_active_waitlist() {
            var waitlist = createWaitlist();
            var failedAt = fixedClock.instant().plusSeconds(1);

            waitlist.fail(failedAt);

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.FAILED);
            assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.PENDING);
            assertThat(waitlist.getState().getFailedAt()).isEqualTo(failedAt);
            assertThat(waitlist.getState().getCancelledAt()).isNull();
            assertThat(waitlist.getState().getExpiredAt()).isNull();
            assertThat(waitlist.getState().getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("활성 알림 전용 대기열은 알림 완료 처리할 수 있다")
        void complete_notify_only_waitlist() {
            var waitlist = createWaitlist(WaitlistBehavior.NOTIFY_ONLY);
            var completedAt = fixedClock.instant().plusSeconds(1);

            waitlist.complete(completedAt);

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
            assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.NOTIFIED);
            assertThat(waitlist.getState().getCompletedAt()).isEqualTo(completedAt);
            assertThat(waitlist.getState().getCancelledAt()).isNull();
            assertThat(waitlist.getState().getExpiredAt()).isNull();
            assertThat(waitlist.getState().getFailedAt()).isNull();
        }

        @Test
        @DisplayName("활성 자동 점유 대기열은 점유 완료 처리할 수 있다")
        void complete_auto_claim_waitlist() {
            var waitlist = createWaitlist(WaitlistBehavior.AUTO_CLAIM);
            var completedAt = fixedClock.instant().plusSeconds(1);

            waitlist.complete(completedAt);

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
            assertThat(waitlist.getState().getResolution()).isEqualTo(WaitlistResolution.CLAIMED);
            assertThat(waitlist.getState().getCompletedAt()).isEqualTo(completedAt);
            assertThat(waitlist.getState().getCancelledAt()).isNull();
            assertThat(waitlist.getState().getExpiredAt()).isNull();
            assertThat(waitlist.getState().getFailedAt()).isNull();
        }

        @Test
        @DisplayName("활성 대기열은 요청 시각 이전으로 처리할 수 없다")
        void can_not_transition_before_requested_at() {
            var waitlist = createWaitlist();
            var beforeRequestedAt = waitlist.getState().getRequestedAt().minusSeconds(1);

            assertThatThrownBy(() -> waitlist.cancel(beforeRequestedAt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("must not be before requestedAt.");
            assertThatThrownBy(() -> waitlist.expire(beforeRequestedAt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("must not be before requestedAt.");
            assertThatThrownBy(() -> waitlist.fail(beforeRequestedAt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("must not be before requestedAt.");
            assertThatThrownBy(() -> waitlist.complete(beforeRequestedAt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("must not be before requestedAt.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Transition from CANCELLED")
    class TransitionFromCancelled {
        @Test
        @DisplayName("취소된 대기열은 다시 취소 처리할 수 없다")
        void can_not_cancel_already_cancelled_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.CANCELLED);

            assertThatThrownBy(() -> waitlist.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
        }

        @Test
        @DisplayName("취소된 대기열은 만료 처리할 수 없다")
        void can_not_expire_cancelled_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.CANCELLED);

            assertThatThrownBy(() -> waitlist.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
        }

        @Test
        @DisplayName("취소된 대기열은 실패 처리할 수 없다")
        void can_not_fail_cancelled_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.CANCELLED);

            assertThatThrownBy(() -> waitlist.fail(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
        }

        @Test
        @DisplayName("취소된 대기열은 완료 처리할 수 없다")
        void can_not_complete_cancelled_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.CANCELLED);

            assertThatThrownBy(() -> waitlist.complete(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Transition from EXPIRED")
    class TransitionFromExpired {
        @Test
        @DisplayName("만료된 대기열은 취소 처리할 수 없다")
        void can_not_cancel_expired_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.EXPIRED);

            assertThatThrownBy(() -> waitlist.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.EXPIRED);
        }

        @Test
        @DisplayName("만료된 대기열은 다시 만료 처리할 수 없다")
        void can_not_expire_already_expired_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.EXPIRED);

            assertThatThrownBy(() -> waitlist.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.EXPIRED);
        }

        @Test
        @DisplayName("만료된 대기열은 실패 처리할 수 없다")
        void can_not_fail_expired_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.EXPIRED);

            assertThatThrownBy(() -> waitlist.fail(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.EXPIRED);
        }

        @Test
        @DisplayName("만료된 대기열은 완료 처리할 수 없다")
        void can_not_complete_expired_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.EXPIRED);

            assertThatThrownBy(() -> waitlist.complete(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.EXPIRED);
        }
    }

    @Nested
    @DisplayName("Transition from FAILED")
    class TransitionFromFailed {
        @Test
        @DisplayName("실패한 대기열은 취소 처리할 수 없다")
        void can_not_cancel_failed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.FAILED);

            assertThatThrownBy(() -> waitlist.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 실패한 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.FAILED);
        }

        @Test
        @DisplayName("실패한 대기열은 만료 처리할 수 없다")
        void can_not_expire_failed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.FAILED);

            assertThatThrownBy(() -> waitlist.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 실패한 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.FAILED);
        }

        @Test
        @DisplayName("실패한 대기열은 다시 실패 처리할 수 없다")
        void can_not_fail_already_failed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.FAILED);

            assertThatThrownBy(() -> waitlist.fail(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 실패한 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.FAILED);
        }

        @Test
        @DisplayName("실패한 대기열은 완료 처리할 수 없다")
        void can_not_complete_failed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.FAILED);

            assertThatThrownBy(() -> waitlist.complete(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 실패한 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("Transition from COMPLETED")
    class TransitionFromCompleted {
        @Test
        @DisplayName("완료된 대기열은 취소 처리할 수 없다")
        void can_not_cancel_completed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.COMPLETED);

            assertThatThrownBy(() -> waitlist.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 완료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
        }

        @Test
        @DisplayName("완료된 대기열은 만료 처리할 수 없다")
        void can_not_expire_completed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.COMPLETED);

            assertThatThrownBy(() -> waitlist.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 완료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
        }

        @Test
        @DisplayName("완료된 대기열은 실패 처리할 수 없다")
        void can_not_fail_completed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.COMPLETED);

            assertThatThrownBy(() -> waitlist.fail(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 완료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
        }

        @Test
        @DisplayName("완료된 대기열은 다시 완료 처리할 수 없다")
        void can_not_complete_already_completed_waitlist() {
            var waitlist = createWaitlist(WaitlistStatus.COMPLETED);

            assertThatThrownBy(() -> waitlist.complete(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 완료된 대기열입니다.");

            assertThat(waitlist.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
        }
    }
}
