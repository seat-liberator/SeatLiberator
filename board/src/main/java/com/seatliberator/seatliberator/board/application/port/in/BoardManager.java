package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardUpdateCommand;

public interface BoardManager {
    BoardEntry create(BoardCreateCommand command);

    BoardEntry update(BoardUpdateCommand command);

    void delete(BoardDeleteCommand command);
}
