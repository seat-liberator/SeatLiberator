package com.seatliberator.seatliberator.reservation.application.room.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindRoomQuery(UUID roomId) {
    public FindRoomQuery {
        Preconditions.requireNonNull(roomId, "roomId");
    }

    public static FindRoomQuery of(UUID roomId) {
        return new FindRoomQuery(roomId);
    }
}
