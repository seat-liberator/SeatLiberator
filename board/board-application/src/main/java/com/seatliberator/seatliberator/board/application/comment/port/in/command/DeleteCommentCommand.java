package com.seatliberator.seatliberator.board.application.comment.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeleteCommentCommand(
        UUID commentId
) {
    public DeleteCommentCommand {
        Preconditions.requireNonNull(commentId, "commentId");
    }

    public static DeleteCommentCommand of(UUID commentId) {
        return new DeleteCommentCommand(commentId);
    }
}
