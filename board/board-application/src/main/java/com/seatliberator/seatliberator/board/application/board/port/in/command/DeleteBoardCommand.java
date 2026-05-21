package com.seatliberator.seatliberator.board.application.board.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeleteBoardCommand(
        UUID boardId
) {
    public DeleteBoardCommand {
        Preconditions.requireNonNull(boardId, "boardId");
    }

    public static DeleteBoardCommand of(UUID boardId) {
        return new DeleteBoardCommand(boardId);
    }
}
