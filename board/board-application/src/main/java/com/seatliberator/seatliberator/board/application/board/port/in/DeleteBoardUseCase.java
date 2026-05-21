package com.seatliberator.seatliberator.board.application.board.port.in;

import com.seatliberator.seatliberator.board.application.board.port.in.command.DeleteBoardCommand;

public interface DeleteBoardUseCase {
    void delete(DeleteBoardCommand command);
}
