package com.seatliberator.seatliberator.reservation.application.reservation.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationState;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

public record ReservationResult(
        UUID reservationId,
        String userId,
        ReservationStateResult state
) {
    public static ReservationResult from(Reservation reservation) {
        return new ReservationResult(
                reservation.getId(),
                reservation.getUserId(),
                ReservationStateResult.from(reservation.getState())
        );
    }

    public record ReservationStateResult(
            ReservationStatus status,
            Instant reservedAt,
            Instant usedAt,
            Instant cancelledAt,
            Instant expiredAt
    ) {
        public static ReservationStateResult from(ReservationState state) {
            return new ReservationStateResult(
                    state.getStatus(),
                    state.getReservedAt(),
                    state.getUsedAt(),
                    state.getCancelledAt(),
                    state.getExpiredAt()
            );
        }
    }
}