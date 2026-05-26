package com.seatliberator.seatliberator.reservation.application.room.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.room.Room;

import java.time.Instant;
import java.util.UUID;

public record RoomResult(
        UUID roomId,
        String code,
        RoomOperationPolicyResult roomOperationPolicy,
        Instant createdAt
) {
    public static RoomResult from(Room room) {
        return new RoomResult(
                room.getId(),
                room.getCode(),
                RoomOperationPolicyResult.from(room.getOperationPolicy()),
                room.getCreatedAt()
        );
    }
}
