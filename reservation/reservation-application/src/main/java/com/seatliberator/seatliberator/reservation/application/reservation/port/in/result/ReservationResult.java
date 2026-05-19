package com.seatliberator.seatliberator.reservation.application.reservation.port.in.result;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

import java.util.UUID;

public record ReservationResult(
        UUID reservationId,
        String userId,
        ReservationStateResult state
) {
    public ReservationResult {
        Preconditions.requireNonNull(reservationId, "reservationId");
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(state, "state");
    }

    public static ReservationResult from(Reservation reservation) {
        return new ReservationResult(
                reservation.getId(),
                reservation.getUserId(),
                ReservationStateResult.from(reservation.getState())
        );
    }
}