package com.seatliberator.seatliberator.board.infrastructure.web.request;

import org.jspecify.annotations.Nullable;

public record CategoryUpdateRequest(
        @Nullable String name,
        @Nullable String description
) {
}
