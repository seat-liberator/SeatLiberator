package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.query.ListBoardPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.BoardPostSummaryResult;

import java.util.List;

public interface ListBoardPostUseCase {
    List<BoardPostSummaryResult> list(ListBoardPostQuery query);
}
