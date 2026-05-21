package com.seatliberator.seatliberator.board.application.board.port.out;

import com.seatliberator.seatliberator.board.domain.Board;

import java.util.Optional;
import java.util.UUID;

public interface BoardReader {
    boolean existsById(UUID id);

    Optional<Board> findById(UUID id);
}
