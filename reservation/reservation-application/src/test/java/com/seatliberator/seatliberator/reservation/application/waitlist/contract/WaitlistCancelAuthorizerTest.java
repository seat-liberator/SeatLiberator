package com.seatliberator.seatliberator.reservation.application.waitlist.contract;

import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.application.waitlist.WaitlistTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("WaitlistCancelAuthorizer 테스트")
public class WaitlistCancelAuthorizerTest {
    WaitlistCancelAuthorizer authorizer;

    @BeforeEach
    void run() {
        authorizer = new WaitlistCancelAuthorizer();
    }

    @Test
    @DisplayName("대기열 관리 권한이 있으면 대기열 취소를 허용한다")
    void accept_when_actor_has_waitlist_manage_capability() {
        var result = authorizer.evaluate(WAITLIST_MANAGER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(WaitlistPolicyReason.WAITLIST_MANAGER);
    }

    @Test
    @DisplayName("대기열 취소 권한이 있으면 대기열 취소를 허용한다")
    void accept_when_actor_has_waitlist_cancel_capability() {
        var result = authorizer.evaluate(WAITLIST_CANCELLER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(WaitlistPolicyReason.AUTHORIZED_WAITLIST_CANCEL);
    }

    @Test
    @DisplayName("대기열 취소 권한이 없으면 정책 거절 예외")
    void throw_exception_when_actor_has_no_waitlist_cancel_capability() {
        assertThatThrownBy(() -> authorizer.validate(ACTOR))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(WaitlistPolicyReason.UNAUTHORIZED_WAITLIST_CANCEL);
    }
}
