package com.seatliberator.seatliberator.reservation.application.reservation.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindReservationQuery(
        UUID reservationId
) {
    public FindReservationQuery {
        Preconditions.requireNonNull(reservationId, "reservationId");
    }

    public static FindReservationQuery of(UUID reservationId) {
        return new FindReservationQuery(reservationId);
    }
}
