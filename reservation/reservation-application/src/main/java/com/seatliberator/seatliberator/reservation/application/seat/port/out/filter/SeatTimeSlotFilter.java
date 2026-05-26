package com.seatliberator.seatliberator.reservation.application.seat.port.out.filter;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record SeatTimeSlotFilter(
        @Nullable UUID seatId
) {
    public static SeatTimeSlotFilter empty() {
        return new SeatTimeSlotFilter(null);
    }

    public SeatTimeSlotFilter seatId(UUID seatId) {
        return new SeatTimeSlotFilter(seatId);
    }
}
