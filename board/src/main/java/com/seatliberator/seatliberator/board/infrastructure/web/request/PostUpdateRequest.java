package com.seatliberator.seatliberator.board.infrastructure.web.request;

import org.jspecify.annotations.Nullable;

public record PostUpdateRequest(
        @Nullable String title,
        @Nullable String content
) {
}
