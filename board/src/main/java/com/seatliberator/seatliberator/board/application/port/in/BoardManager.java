package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;

public interface BoardManager {
    BoardEntry create(BoardCreateCommand command);

    void delete(BoardDeleteCommand command);
}
