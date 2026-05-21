package com.seatliberator.seatliberator.board.application.comment.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindCommentQuery(
        UUID commentId
) {
    public FindCommentQuery {
        Preconditions.requireNonNull(commentId, "commentId");
    }

    public static FindCommentQuery of(UUID commentId) {
        return new FindCommentQuery(commentId);
    }
}
