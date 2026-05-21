package com.seatliberator.seatliberator.board.application.post.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeletePostCommand(
        UUID postId
) {
    public DeletePostCommand {
        Preconditions.requireNonNull(postId, "postId");
    }

    public static DeletePostCommand of(UUID postId) {
        return new DeletePostCommand(postId);
    }
}
