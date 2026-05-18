package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.application.DefaultTestSupport.ACTOR;
import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.BOOKING_MANAGER;
import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.BOOKING_READER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ReservationReadAuthorizer 테스트")
public class ReservationReadAuthorizerTest {
    ReservationReadAuthorizer authorizer;

    @BeforeEach
    void run() {
        authorizer = new ReservationReadAuthorizer();
    }

    @Test
    @DisplayName("예약 관리 권한이 있으면 조회 허용")
    void accept_when_actor_has_booking_manage_capability() {
        var result = authorizer.evaluate(BOOKING_MANAGER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.RESERVATION_MANAGER);
    }

    @Test
    @DisplayName("예약 조회 권한이 있으면 조회 허용")
    void accept_when_actor_has_booking_read_capability() {
        var result = authorizer.evaluate(BOOKING_READER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.AUTHORIZED_RESERVATION_READ);
    }

    @Test
    @DisplayName("예약 조회 권한이 없으면 정책 거절 예외")
    void throw_exception_when_actor_has_no_booking_read_capability() {
        assertThatThrownBy(() -> authorizer.validate(ACTOR))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
    }
}
