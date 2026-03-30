package com.seatliberator.seatliberator.board.application.port.in.command;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record CategoryUpdateCommand(
        UUID boardId,
        UUID categoryId,
        @Nullable String name,
        @Nullable String description
) {
}
