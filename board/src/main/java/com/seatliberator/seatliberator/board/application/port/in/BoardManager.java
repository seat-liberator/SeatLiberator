package com.seatliberator.seatliberator.board.application.port.in;

import java.util.UUID;

public interface BoardManager {
    BoardEntry create(String name, String description);

    void remove(UUID boardId);
}
