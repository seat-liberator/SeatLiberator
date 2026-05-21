package com.seatliberator.seatliberator.board.application.post.port.in.result;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.Instant;
import java.util.UUID;

public record BoardPostSummaryResult(
        UUID boardId,
        UUID postId,
        UUID categoryId,
        String categoryName,
        String userId,
        String title,
        Instant createdAt,
        Instant updatedAt,
        long commentCount
) {
    public BoardPostSummaryResult {
        Preconditions.requireNonNull(boardId, "boardId");
        Preconditions.requireNonNull(postId, "postId");
        Preconditions.requireNonNull(categoryId, "categoryId");
        Preconditions.requireNonBlank(categoryName, "categoryName");
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonBlank(title, "title");
        Preconditions.requireNonNull(createdAt, "createdAt");
    }
}
