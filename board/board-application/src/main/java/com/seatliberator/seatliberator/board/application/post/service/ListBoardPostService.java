package com.seatliberator.seatliberator.board.application.post.service;

import com.seatliberator.seatliberator.board.application.post.port.in.ListBoardPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.query.ListBoardPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.BoardPostSummaryResult;
import com.seatliberator.seatliberator.board.application.post.port.out.BoardPostSummaryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListBoardPostService implements ListBoardPostUseCase {
    private final BoardPostSummaryReader reader;

    @Override
    public List<BoardPostSummaryResult> list(ListBoardPostQuery query) {
        return reader.findByFilter(query.toFilter());
    }
}
