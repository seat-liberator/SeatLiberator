package com.seatliberator.seatliberator.reservation.application.seat.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record ListSeatQuery(
        UUID roomId
) {
    public ListSeatQuery {
        Preconditions.requireNonNull(roomId, "roomId");
    }

    public static ListSeatQuery of(UUID roomId) {
        return new ListSeatQuery(roomId);
    }
}
