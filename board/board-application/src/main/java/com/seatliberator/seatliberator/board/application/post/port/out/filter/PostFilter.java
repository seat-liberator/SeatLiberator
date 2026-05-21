package com.seatliberator.seatliberator.board.application.post.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record PostFilter(
        @Nullable UUID boardId,
        @Nullable UUID categoryId,
        @Nullable String userId
) {
    public static PostFilter empty() {
        return new PostFilter(null, null, null);
    }

    public PostFilter boardId(UUID boardId) {
        Preconditions.requireNonNull(boardId, "boardId");

        return new PostFilter(boardId, categoryId, userId);
    }

    public PostFilter categoryId(UUID categoryId) {
        Preconditions.requireNonNull(categoryId, "categoryId");

        return new PostFilter(boardId, categoryId, userId);
    }

    public PostFilter userId(String userId) {
        Preconditions.requireNonBlank(userId, "userId");

        return new PostFilter(boardId, categoryId, userId);
    }
}
