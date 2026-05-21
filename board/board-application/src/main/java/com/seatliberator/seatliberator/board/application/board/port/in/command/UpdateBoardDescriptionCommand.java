package com.seatliberator.seatliberator.board.application.board.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateBoardDescriptionCommand(
        UUID boardId,
        String description
) {
    public UpdateBoardDescriptionCommand {
        Preconditions.requireNonNull(boardId, "boardId");
        Preconditions.requireNonBlank(description, "description");
    }

    public static UpdateBoardDescriptionCommand of(UUID boardId, String description) {
        return new UpdateBoardDescriptionCommand(boardId, description);
    }
}
