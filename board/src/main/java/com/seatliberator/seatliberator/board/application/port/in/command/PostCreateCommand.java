package com.seatliberator.seatliberator.board.application.port.in.command;

import java.util.UUID;

public record PostCreateCommand(
        UUID boardId,
        String title,
        String content
) {
}
