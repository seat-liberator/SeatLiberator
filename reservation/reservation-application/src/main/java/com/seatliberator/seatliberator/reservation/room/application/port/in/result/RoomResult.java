package com.seatliberator.seatliberator.reservation.room.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;

import java.time.Instant;

public record RoomResult(
        String id,
        Instant createdAt
) {
    public static RoomResult from(Room room) {
        return new RoomResult(
                room.getRoomId(),
                room.getCreatedAt()
        );
    }
}
