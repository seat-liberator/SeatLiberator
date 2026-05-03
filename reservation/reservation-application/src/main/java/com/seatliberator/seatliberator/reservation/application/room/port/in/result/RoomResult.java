package com.seatliberator.seatliberator.reservation.application.room.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.room.Room;

import java.time.Instant;

public record RoomResult(
        String roomId,
        Instant createdAt
) {
    public static RoomResult from(Room room) {
        return new RoomResult(
                room.getRoomId(),
                room.getCreatedAt()
        );
    }
}
