package com.seatliberator.seatliberator.board.application.post.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdatePostCategoryCommand(
        UUID postId,
        UUID categoryId
) {
    public UpdatePostCategoryCommand {
        Preconditions.requireNonNull(postId, "postId");
        Preconditions.requireNonNull(categoryId, "categoryId");
    }

    public static UpdatePostCategoryCommand of(UUID postId, UUID categoryId) {
        return new UpdatePostCategoryCommand(postId, categoryId);
    }
}
