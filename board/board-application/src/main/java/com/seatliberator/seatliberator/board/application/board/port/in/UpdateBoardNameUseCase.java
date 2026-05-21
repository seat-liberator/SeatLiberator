package com.seatliberator.seatliberator.board.application.board.port.in;

import com.seatliberator.seatliberator.board.application.board.port.in.command.UpdateBoardNameCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;

public interface UpdateBoardNameUseCase {
    BoardResult update(UpdateBoardNameCommand command);
}
