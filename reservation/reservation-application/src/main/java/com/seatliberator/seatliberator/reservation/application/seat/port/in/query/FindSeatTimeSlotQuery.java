package com.seatliberator.seatliberator.reservation.application.seat.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindSeatTimeSlotQuery(
        UUID seatTimeSlotId
) {
    public FindSeatTimeSlotQuery {
        Preconditions.requireNonNull(seatTimeSlotId, "seatTimeSlotId");
    }
}
