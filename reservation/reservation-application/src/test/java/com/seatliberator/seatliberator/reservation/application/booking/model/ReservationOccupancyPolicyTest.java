package com.seatliberator.seatliberator.reservation.application.booking.model;

import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reservation Occupancy Policy")
public class ReservationOccupancyPolicyTest {
    ReservationOccupancyPolicy policy = new ReservationOccupancyPolicy();

    @Test
    @DisplayName("RESERVED, USED 상태는 점유 상태로 판단한다")
    void return_true_for_reserved_and_used() {
        assertThat(policy.isOccupied(ReservationStatus.RESERVED)).isTrue();
        assertThat(policy.isOccupied(ReservationStatus.USED)).isTrue();
    }

    @Test
    @DisplayName("CANCELED, EXPIRED 상태는 비점유 상태로 판단한다")
    void unoccupied_when_canceled_and_expired() {
        assertThat(policy.isOccupied(ReservationStatus.CANCELLED)).isFalse();
        assertThat(policy.isOccupied(ReservationStatus.EXPIRED)).isFalse();
    }
}
