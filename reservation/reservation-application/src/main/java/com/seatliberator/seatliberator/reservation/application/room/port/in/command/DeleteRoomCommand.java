package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeleteRoomCommand(UUID roomId) {
    public DeleteRoomCommand {
        Preconditions.requireNonNull(roomId, "roomId");
    }

    public static DeleteRoomCommand of(UUID roomId) {
        return new DeleteRoomCommand(roomId);
    }
}
