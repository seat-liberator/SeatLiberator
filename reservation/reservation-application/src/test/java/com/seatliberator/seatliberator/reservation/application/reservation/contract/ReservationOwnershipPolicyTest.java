package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationOwnershipPolicy 테스트")
public class ReservationOwnershipPolicyTest {
    @Mock
    ReservationReader reader;

    ReservationOwnershipPolicy policy;

    @BeforeEach
    void run() {
        policy = new ReservationOwnershipPolicy(reader);
    }

    @Test
    @DisplayName("현재 actor가 예약 소유자이면 예약 접근을 허용한다")
    void accept_when_actor_is_reservation_owner() {
        var reservation = reservationWithId();

        var result = policy.evaluate(reservation, ACTOR);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.RESERVATION_OWNER);
    }

    @Test
    @DisplayName("예약 관리 권한이 있으면 다른 사용자의 예약 접근을 허용한다")
    void accept_when_actor_has_booking_manage_capability() {
        var reservation = reservationWithId();

        var result = policy.evaluate(reservation, BOOKING_MANAGER);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.RESERVATION_MANAGER);
    }

    @Test
    @DisplayName("현재 actor가 예약 소유자가 아니면 정책 거절 예외")
    void throw_exception_when_actor_is_not_reservation_owner() {
        var reservation = reservationWithId();

        assertThatThrownBy(() -> policy.validate(reservation, OTHER_ACTOR))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    @Test
    @DisplayName("예약 ID 검증 시 reader에서 예약을 조회하고 소유권을 검증한다")
    void validate_by_id_finds_reservation_and_validates_owner() {
        var reservation = reservationWithId();

        when(reader.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        policy.validate(RESERVATION_ID, ACTOR);

        verify(reader, only()).findById(RESERVATION_ID);
    }

    @Test
    @DisplayName("예약 ID 검증 시 예약을 찾을 수 없으면 RESERVATION_NOT_FOUND 예외")
    void throw_exception_when_reservation_not_found() {
        when(reader.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policy.validate(RESERVATION_ID, ACTOR))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);

        verify(reader, only()).findById(RESERVATION_ID);
    }
}
