package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateRoomCodeCommand(
        UUID roomId,
        String newCode
) {
    public UpdateRoomCodeCommand {
        Preconditions.requireNonNull(roomId, "roomId");
        Preconditions.requireNonBlank(newCode, "newCode");
    }

    public static UpdateRoomCodeCommand of(UUID roomId, String newCode) {
        return new UpdateRoomCodeCommand(roomId, newCode);
    }
}
