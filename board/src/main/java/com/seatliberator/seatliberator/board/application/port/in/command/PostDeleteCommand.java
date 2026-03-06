package com.seatliberator.seatliberator.board.application.port.in.command;

import java.util.UUID;

public record PostDeleteCommand(
        UUID boardId,
        UUID postId
) {
}
