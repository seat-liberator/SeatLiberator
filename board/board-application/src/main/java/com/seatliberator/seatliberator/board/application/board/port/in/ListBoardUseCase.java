package com.seatliberator.seatliberator.board.application.board.port.in;

import com.seatliberator.seatliberator.board.application.board.port.in.query.ListBoardQuery;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;

import java.util.List;

public interface ListBoardUseCase {
    List<BoardResult> list(ListBoardQuery query);
}
