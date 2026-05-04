package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreateAuthorizedPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.booking.contract.service.DefaultReservationCreateAuthorizedPolicy;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReservationCreateAuthorizedPolicy 테스트")
public class DefaultReservationCreateAuthorizedPolicyTest {

    ReservationCreateAuthorizedPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DefaultReservationCreateAuthorizedPolicy();
    }

    @Test
    @DisplayName("예약 관리 권한이 있으면 승인한다")
    void accept_when_requester_has_booking_manage_capability() {
        var requester = new ActorFixture.Builder()
                .capabilities(Set.of(ReservationCapability.BOOKING_MANAGE))
                .build();

        var result = policy.evaluate(requester);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.RESERVATION_MANAGER);
    }

    @Test
    @DisplayName("예약 생성 권한이 있으면 승인한다")
    void accept_when_requester_has_booking_create_capability() {
        var requester = new ActorFixture.Builder()
                .capabilities(Set.of(ReservationCapability.BOOKING_CREATE))
                .build();

        var result = policy.evaluate(requester);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.AUTHORIZED_RESERVATION_CREATE);
    }

    @Test
    @DisplayName("예약 생성 권한이 없으면 거절한다")
    void reject_when_requester_has_no_booking_create_capability() {
        var requester = new ActorFixture.Builder()
                .capabilities(Set.of())
                .build();

        var result = policy.evaluate(requester);

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE);
    }
}
