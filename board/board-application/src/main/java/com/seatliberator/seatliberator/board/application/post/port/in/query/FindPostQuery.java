package com.seatliberator.seatliberator.board.application.post.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindPostQuery(
        UUID postId
) {
    public FindPostQuery {
        Preconditions.requireNonNull(postId, "postId");
    }

    public static FindPostQuery of(UUID postId) {
        return new FindPostQuery(postId);
    }
}
