package com.seatliberator.seatliberator.board.application.post.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdatePostContentCommand(
        UUID postId,
        String content
) {
    public UpdatePostContentCommand {
        Preconditions.requireNonNull(postId, "postId");
        Preconditions.requireNonBlank(content, "content");
    }

    public static UpdatePostContentCommand of(UUID postId, String content) {
        return new UpdatePostContentCommand(postId, content);
    }
}
