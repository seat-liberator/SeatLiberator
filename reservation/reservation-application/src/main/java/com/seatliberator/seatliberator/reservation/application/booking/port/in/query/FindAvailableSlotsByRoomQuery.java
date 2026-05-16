package com.seatliberator.seatliberator.reservation.application.booking.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRange;

import java.util.UUID;

public record FindAvailableSlotsByRoomQuery(
        UUID roomId,
        DateRange range
) {
    public FindAvailableSlotsByRoomQuery {
        Preconditions.requireNonNull(roomId, "roomId");
        Preconditions.requireNonNull(range, "range");
    }

    public static FindAvailableSlotsByRoomQuery of(UUID roomId, DateRange range) {
        return new FindAvailableSlotsByRoomQuery(roomId, range);
    }
}
