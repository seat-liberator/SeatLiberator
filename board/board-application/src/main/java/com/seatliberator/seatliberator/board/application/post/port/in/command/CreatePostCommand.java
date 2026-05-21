package com.seatliberator.seatliberator.board.application.post.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CreatePostCommand(
        UUID boardId,
        UUID categoryId,
        String userId,
        String title,
        String content
) {
    public CreatePostCommand {
        Preconditions.requireNonNull(boardId, "boardId");
        Preconditions.requireNonNull(categoryId, "categoryId");
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonBlank(title, "title");
        Preconditions.requireNonBlank(content, "content");
    }

    public static CreatePostCommand of(UUID boardId, UUID categoryId, String userId, String title, String content) {
        return new CreatePostCommand(boardId, categoryId, userId, title, content);
    }
}
