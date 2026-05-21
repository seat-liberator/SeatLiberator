package com.seatliberator.seatliberator.board.application.board.service;

import com.seatliberator.seatliberator.board.application.board.port.in.CreateBoardUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.DeleteBoardUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.UpdateBoardDescriptionUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.UpdateBoardNameUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.command.CreateBoardCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.command.DeleteBoardCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.command.UpdateBoardDescriptionCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.command.UpdateBoardNameCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;
import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.board.port.out.BoardStore;
import com.seatliberator.seatliberator.board.application.shared.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandService implements
        CreateBoardUseCase,
        UpdateBoardNameUseCase,
        UpdateBoardDescriptionUseCase,
        DeleteBoardUseCase {

    private final BoardReader reader;
    private final BoardStore store;
    private final Clock clock;

    @Override
    public BoardResult create(CreateBoardCommand command) {
        var now = clock.instant();
        var board = Board.of(
                command.name(),
                command.description(),
                now
        );

        var saved = store.save(board);

        return BoardResult.from(saved);
    }

    @Override
    public void delete(DeleteBoardCommand command) {
        var boardId = command.boardId();
        var board = reader.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        store.delete(board);
    }

    @Override
    public BoardResult update(UpdateBoardDescriptionCommand command) {
        var boardId = command.boardId();

        var board = reader.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        board.updateDescription(command.description());

        var saved = store.save(board);

        return BoardResult.from(saved);
    }

    @Override
    public BoardResult update(UpdateBoardNameCommand command) {
        var boardId = command.boardId();

        var board = reader.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        board.updateName(command.name());

        var saved = store.save(board);

        return BoardResult.from(saved);
    }
}
