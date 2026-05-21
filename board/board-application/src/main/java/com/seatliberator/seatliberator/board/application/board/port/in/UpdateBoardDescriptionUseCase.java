package com.seatliberator.seatliberator.board.application.board.port.in;

import com.seatliberator.seatliberator.board.application.board.port.in.command.UpdateBoardDescriptionCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;

public interface UpdateBoardDescriptionUseCase {
    BoardResult update(UpdateBoardDescriptionCommand command);
}
