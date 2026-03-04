package com.seatliberator.seatliberator.board.infrastructure.jpa;

import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID>, BoardStore {
    @Override
    default void remove(UUID boardId) {
        deleteById(boardId);
    }
}
