package com.seatliberator.seatliberator.board.application.service;

import com.seatliberator.seatliberator.board.application.port.in.BoardEntry;
import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService implements BoardManager {
    private final BoardStore boardStore;

    @Override
    public BoardEntry create(BoardCreateCommand command) {
        var board = Board.create(
                command.name(),
                command.description()
        );
        boardStore.save(board);
        return BoardEntry.of(board);
    }

    @Override
    public void delete(BoardDeleteCommand command) {
        var boardId = command.boardId();
        boardStore.remove(boardId);
    }
}
