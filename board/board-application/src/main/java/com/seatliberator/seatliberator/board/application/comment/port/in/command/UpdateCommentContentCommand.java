package com.seatliberator.seatliberator.board.application.comment.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateCommentContentCommand(
        UUID commentId,
        String content
) {
    public UpdateCommentContentCommand {
        Preconditions.requireNonNull(commentId, "commentId");
        Preconditions.requireNonBlank(content, "content");
    }

    public static UpdateCommentContentCommand of(UUID commentId, String content) {
        return new UpdateCommentContentCommand(commentId, content);
    }
}
