package com.seatliberator.seatliberator.reservation.application.seat.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindSeatQuery(UUID seatId) {
    public FindSeatQuery {
        Preconditions.requireNonNull(seatId, "seatId");
    }

    public static FindSeatQuery of(UUID seatId) {
        return new FindSeatQuery(seatId);
    }
}
