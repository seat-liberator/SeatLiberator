package com.seatliberator.seatliberator.board.application.port.in.command;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record PostUpdateCommand(
        UUID boardId,
        UUID postId,
        @Nullable String title,
        @Nullable String content
) {
}
