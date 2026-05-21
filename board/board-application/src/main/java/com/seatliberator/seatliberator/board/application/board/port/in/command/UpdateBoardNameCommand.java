package com.seatliberator.seatliberator.board.application.board.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateBoardNameCommand(
        UUID boardId,
        String name
) {
    public UpdateBoardNameCommand {
        Preconditions.requireNonNull(boardId, "boardId");
        Preconditions.requireNonBlank(name, "name");
    }

    public static UpdateBoardNameCommand of(UUID boardId, String name) {
        return new UpdateBoardNameCommand(boardId, name);
    }
}
