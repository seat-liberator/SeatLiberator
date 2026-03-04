package com.seatliberator.seatliberator.board.application.service;

import com.seatliberator.seatliberator.board.application.port.in.BoardEntry;
import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService implements BoardManager {
    private final BoardStore boardStore;

    @Override
    public BoardEntry create(String name, String description) {
        var board = Board.create(name, description);
        boardStore.save(board);
        return BoardEntry.of(board);
    }

    @Override
    public void remove(UUID boardId) {
        boardStore.remove(boardId);
    }
}
