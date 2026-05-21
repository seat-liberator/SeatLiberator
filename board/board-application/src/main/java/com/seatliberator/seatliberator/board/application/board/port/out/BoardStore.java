package com.seatliberator.seatliberator.board.application.board.port.out;

import com.seatliberator.seatliberator.board.domain.Board;

public interface BoardStore {
    Board save(Board board);

    void delete(Board board);
}
