package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.domain.Board;

import java.util.UUID;

public record BoardEntry(
        UUID boardId,
        String name,
        String description
) {
    public static BoardEntry of(Board board) {
        return new BoardEntry(board.getId(), board.getName(), board.getDescription());
    }
}
