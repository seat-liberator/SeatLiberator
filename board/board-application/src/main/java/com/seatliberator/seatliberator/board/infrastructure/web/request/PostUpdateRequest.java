package com.seatliberator.seatliberator.board.infrastructure.web.request;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record PostUpdateRequest(
        @Nullable UUID categoryId,
        @Nullable String title,
        @Nullable String content
) {
}
