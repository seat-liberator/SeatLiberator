package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record CreateRoomCommand(String code) {
    public CreateRoomCommand {
        Preconditions.requireNonNull(code, "code");
    }

    public static CreateRoomCommand of(String code) {
        return new CreateRoomCommand(code);
    }
}
