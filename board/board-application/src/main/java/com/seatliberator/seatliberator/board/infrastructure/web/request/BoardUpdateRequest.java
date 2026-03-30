package com.seatliberator.seatliberator.board.infrastructure.web.request;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record BoardUpdateRequest(
        UUID boardId,
        @Nullable String name,
        @Nullable String description
) {
}
