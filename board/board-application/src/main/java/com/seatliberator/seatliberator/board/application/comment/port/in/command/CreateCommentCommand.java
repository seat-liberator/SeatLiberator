package com.seatliberator.seatliberator.board.application.comment.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CreateCommentCommand(
        UUID postId,
        String userId,
        String content
) {
    public CreateCommentCommand {
        Preconditions.requireNonNull(postId, "postId");
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonBlank(content, "content");
    }

    public static CreateCommentCommand of(UUID postId, String userId, String content) {
        return new CreateCommentCommand(postId, userId, content);
    }
}
