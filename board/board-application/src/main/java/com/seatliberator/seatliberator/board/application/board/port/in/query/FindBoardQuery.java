package com.seatliberator.seatliberator.board.application.board.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindBoardQuery(
        UUID boardId
) {
    public FindBoardQuery {
        Preconditions.requireNonNull(boardId, "boardId");
    }

    public static FindBoardQuery of(UUID boardId) {
        return new FindBoardQuery(boardId);
    }
}
