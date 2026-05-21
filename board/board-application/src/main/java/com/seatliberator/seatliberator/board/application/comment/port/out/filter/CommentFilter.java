package com.seatliberator.seatliberator.board.application.comment.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record CommentFilter(
        @Nullable UUID postId,
        @Nullable String userId
) {
    public static CommentFilter empty() {
        return new CommentFilter(null, null);
    }

    public CommentFilter postId(UUID postId) {
        Preconditions.requireNonNull(postId, "postId");

        return new CommentFilter(postId, userId);
    }

    public CommentFilter userId(String userId) {
        Preconditions.requireNonBlank(userId, "userId");

        return new CommentFilter(postId, userId);
    }
}