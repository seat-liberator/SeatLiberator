package com.seatliberator.seatliberator.board.application.board.port.in;

import com.seatliberator.seatliberator.board.application.board.port.in.command.CreateBoardCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;

public interface CreateBoardUseCase {
    BoardResult create(CreateBoardCommand command);
}
