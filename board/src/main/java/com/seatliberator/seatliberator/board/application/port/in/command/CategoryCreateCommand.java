package com.seatliberator.seatliberator.board.application.port.in.command;

import java.util.UUID;

public record CategoryCreateCommand(
        UUID boardId,
        String name,
        String description
) {
}
