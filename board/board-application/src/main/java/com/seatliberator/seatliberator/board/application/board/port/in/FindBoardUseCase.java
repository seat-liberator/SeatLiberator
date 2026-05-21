package com.seatliberator.seatliberator.board.application.board.port.in;

import com.seatliberator.seatliberator.board.application.board.port.in.query.FindBoardQuery;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;

public interface FindBoardUseCase {
    BoardResult find(FindBoardQuery query);
}
