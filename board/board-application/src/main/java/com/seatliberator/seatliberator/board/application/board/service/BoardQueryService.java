package com.seatliberator.seatliberator.board.application.board.service;

import com.seatliberator.seatliberator.board.application.board.port.in.FindBoardUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.ListBoardUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.query.FindBoardQuery;
import com.seatliberator.seatliberator.board.application.board.port.in.query.ListBoardQuery;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;
import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.shared.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryService implements
        FindBoardUseCase,
        ListBoardUseCase {

    private final BoardReader reader;

    @Override
    public BoardResult find(FindBoardQuery query) {
        var boardId = query.boardId();
        var board = reader.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        return BoardResult.from(board);
    }

    @Override
    public List<BoardResult> list(ListBoardQuery query) {
        return reader.findByFilter(query.toFilter()).stream()
                .map(BoardResult::from)
                .toList();
    }
}
