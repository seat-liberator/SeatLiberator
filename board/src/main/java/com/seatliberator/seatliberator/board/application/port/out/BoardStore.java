package com.seatliberator.seatliberator.board.application.port.out;

import com.seatliberator.seatliberator.board.domain.Board;

import java.util.UUID;

public interface BoardStore {
    Board save(Board board);

    void remove(UUID boardId);
}
