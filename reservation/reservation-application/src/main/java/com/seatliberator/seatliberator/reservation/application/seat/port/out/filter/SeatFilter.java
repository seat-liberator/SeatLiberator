package com.seatliberator.seatliberator.reservation.application.seat.port.out.filter;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record SeatFilter(
        @Nullable UUID roomId
) {
    public static SeatFilter empty() {
        return new SeatFilter(null);
    }

    public SeatFilter roomId(UUID roomId) {
        return new SeatFilter(roomId);
    }
}
