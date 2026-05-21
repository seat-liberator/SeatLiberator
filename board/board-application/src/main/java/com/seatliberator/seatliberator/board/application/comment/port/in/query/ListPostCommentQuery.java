package com.seatliberator.seatliberator.board.application.comment.port.in.query;

import com.seatliberator.seatliberator.board.application.comment.port.out.filter.CommentFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record ListPostCommentQuery(
        UUID postId
) {
    public ListPostCommentQuery {
        Preconditions.requireNonNull(postId, "postId");
    }

    public static ListPostCommentQuery of(UUID postId) {
        return new ListPostCommentQuery(postId);
    }

    public CommentFilter toFilter() {
        return CommentFilter.empty()
                .postId(postId);
    }
}
