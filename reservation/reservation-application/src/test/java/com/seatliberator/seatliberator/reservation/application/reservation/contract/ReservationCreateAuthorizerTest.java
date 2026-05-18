package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ReservationCreateAuthorizer 테스트")
public class ReservationCreateAuthorizerTest {
    ReservationCreateAuthorizer authorizer;

    @BeforeEach
    void run() {
        authorizer = new ReservationCreateAuthorizer();
    }

    @Test
    @DisplayName("예약 관리 권한이 있으면 예약 생성을 허용한다")
    void accept_when_actor_has_booking_manage_capability() {
        var result = authorizer.evaluate(BOOKING_MANAGER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.RESERVATION_MANAGER);
    }

    @Test
    @DisplayName("예약 생성 권한이 있으면 예약 생성을 허용한다")
    void accept_when_actor_has_booking_create_capability() {
        var result = authorizer.evaluate(BOOKING_CREATOR);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.AUTHORIZED_RESERVATION_CREATE);
    }

    @Test
    @DisplayName("예약 생성 권한이 없으면 정책 거절 예외")
    void throw_exception_when_actor_has_no_booking_create_capability() {
        assertThatThrownBy(() -> authorizer.validate(ACTOR))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE);
    }
}
