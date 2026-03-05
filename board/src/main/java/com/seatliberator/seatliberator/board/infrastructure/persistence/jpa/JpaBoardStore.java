package com.seatliberator.seatliberator.board.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import com.seatliberator.seatliberator.board.infrastructure.persistence.jpa.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBoardStore implements BoardStore {
    private final BoardRepository repository;

    @Override
    public Board save(Board board) {
        return repository.save(board);
    }

    @Override
    public void remove(UUID boardId) {
        repository.deleteById(boardId);
    }

    @Override
    public Optional<Board> getSingle(UUID boardId) {
        return repository.findById(boardId);
    }
}
