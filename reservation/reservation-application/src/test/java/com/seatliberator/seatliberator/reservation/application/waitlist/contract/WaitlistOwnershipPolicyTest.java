package com.seatliberator.seatliberator.reservation.application.waitlist.contract;

import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.application.waitlist.WaitlistTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WaitlistOwnershipPolicy 테스트")
public class WaitlistOwnershipPolicyTest {
    @Mock
    WaitlistReader reader;

    WaitlistOwnershipPolicy policy;

    @BeforeEach
    void run() {
        policy = new WaitlistOwnershipPolicy(reader);
    }

    @Test
    @DisplayName("현재 actor가 대기열 소유자이면 대기열 접근을 허용한다")
    void accept_when_actor_is_waitlist_owner() {
        var waitlist = waitlist();

        var result = policy.evaluate(waitlist, ACTOR);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(WaitlistPolicyReason.WAITLIST_OWNER);
    }

    @Test
    @DisplayName("대기열 관리 권한이 있으면 다른 사용자의 대기열 접근을 허용한다")
    void accept_when_actor_has_waitlist_manage_capability() {
        var waitlist = waitlist();

        var result = policy.evaluate(waitlist, WAITLIST_MANAGER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(WaitlistPolicyReason.WAITLIST_MANAGER);
    }

    @Test
    @DisplayName("현재 actor가 대기열 소유자가 아니면 정책 거절 예외")
    void throw_exception_when_actor_is_not_waitlist_owner() {
        var waitlist = waitlist();

        assertThatThrownBy(() -> policy.validate(waitlist, OTHER_ACTOR))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(WaitlistPolicyReason.UNAUTHORIZED_WAITLIST_ACCESS);
    }

    @Test
    @DisplayName("대기열 ID 검증 시 reader에서 대기열을 조회하고 소유권을 검증한다")
    void validate_by_id_finds_waitlist_and_validates_owner() {
        var waitlist = waitlist();

        when(reader.findById(WAITLIST_ID)).thenReturn(Optional.of(waitlist));

        policy.validate(WAITLIST_ID, ACTOR);

        verify(reader, only()).findById(WAITLIST_ID);
    }

    @Test
    @DisplayName("대기열 ID 검증 시 대기열을 찾을 수 없으면 WAITLIST_NOT_FOUND 예외")
    void throw_exception_when_waitlist_not_found() {
        when(reader.findById(WAITLIST_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policy.validate(WAITLIST_ID, ACTOR))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.WAITLIST_NOT_FOUND);

        verify(reader, only()).findById(WAITLIST_ID);
    }
}
