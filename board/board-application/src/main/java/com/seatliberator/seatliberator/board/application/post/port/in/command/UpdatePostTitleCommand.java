package com.seatliberator.seatliberator.board.application.post.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdatePostTitleCommand(
        UUID postId,
        String title
) {
    public UpdatePostTitleCommand {
        Preconditions.requireNonNull(postId, "postId");
        Preconditions.requireNonBlank(title, "title");
    }

    public static UpdatePostTitleCommand of(UUID postId, String title) {
        return new UpdatePostTitleCommand(postId, title);
    }
}
