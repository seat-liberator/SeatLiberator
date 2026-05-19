package com.seatliberator.seatliberator.reservation.application.booking.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindBookingQuery(
        UUID reservationId
) {
    public FindBookingQuery {
        Preconditions.requireNonNull(reservationId, "reservationId");
    }

    public static FindBookingQuery of(UUID reservationId) {
        return new FindBookingQuery(reservationId);
    }
}
