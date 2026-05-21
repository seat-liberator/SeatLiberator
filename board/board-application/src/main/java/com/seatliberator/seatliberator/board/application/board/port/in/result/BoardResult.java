package com.seatliberator.seatliberator.board.application.board.port.in.result;

import com.seatliberator.seatliberator.board.domain.Board;

import java.time.Instant;
import java.util.UUID;

public record BoardResult(
        UUID boardId,
        String name,
        String description,
        Instant createdAt
) {
    public static BoardResult from(Board board) {
        return new BoardResult(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getCreatedAt()
        );
    }
}
