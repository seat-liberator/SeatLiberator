package com.seatliberator.seatliberator.reservation.application.seat.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record ListSeatTimeSlotQuery(
        UUID seatId
) {
    public ListSeatTimeSlotQuery {
        Preconditions.requireNonNull(seatId, "seatId");
    }
}
