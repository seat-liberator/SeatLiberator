package com.seatliberator.seatliberator.reservation.application.booking.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRange;

import java.util.UUID;

public record FindAvailableSlotsBySeatQuery(
        UUID seatId,
        DateRange range
) {
    public FindAvailableSlotsBySeatQuery {
        Preconditions.requireNonNull(seatId, "seatId");
        Preconditions.requireNonNull(range, "range");
    }

    public static FindAvailableSlotsBySeatQuery of(UUID seatId, DateRange range) {
        return new FindAvailableSlotsBySeatQuery(seatId, range);
    }
}
