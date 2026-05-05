package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultReservationOwnershipPolicy 테스트")
public class DefaultReservationOwnershipPolicyTest {
    ReservationOwnershipPolicy ownershipPolicy;

    @BeforeEach
    void run() {
        ownershipPolicy = new DefaultReservationOwnershipPolicy();
    }

    @Test
    @DisplayName("예약의 userId와 요청 userId가 같으면 accepted")
    void accepted_when_reservation_userId_same_with_requester_userId() {
        var userId = "user-1";
        var actor = new ActorFixture.Builder()
                .subject(userId)
                .build();
        var reservation = new ReservationFixture.Builder()
                .userId(userId)
                .build();

        var ownership = ownershipPolicy.evaluate(reservation, actor);

        assertThat(ownership.accepted()).isTrue();
        assertThat(ownership.rejected()).isFalse();
    }

    @Test
    @DisplayName("예약의 userId와 요청 userId가 다르면 접근 권한 없음 이유로 rejected")
    void access_rejected_when_reservation_userId_diff_with_requester_userId() {
        var actor = new ActorFixture.Builder()
                .subject("user-1")
                .build();
        var reservation = new ReservationFixture.Builder()
                .userId("user-2")
                .build();

        var ownership = ownershipPolicy.evaluate(reservation, actor);

        assertThat(ownership.accepted()).isFalse();
        assertThat(ownership.rejected()).isTrue();
        assertThat(ownership.reason()).isEqualTo(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
    }

    @Test
    @DisplayName("사용자가 BOOKING_MANAGE 권한이 있으면 userId가 달라도 accepted")
    void accepted_when_user_has_BOOKING_MANAGE_capability() {
        var actor = new ActorFixture.Builder()
                .subject("user-1")
                .capabilities(Set.of(ReservationCapability.BOOKING_MANAGE))
                .build();
        var reservation = new ReservationFixture.Builder()
                .userId("user-2")
                .build();

        var ownership = ownershipPolicy.evaluate(reservation, actor);

        assertThat(ownership.accepted()).isTrue();
        assertThat(ownership.rejected()).isFalse();
    }
}
