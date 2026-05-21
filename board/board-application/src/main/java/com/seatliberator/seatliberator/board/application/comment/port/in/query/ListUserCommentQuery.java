package com.seatliberator.seatliberator.board.application.comment.port.in.query;

import com.seatliberator.seatliberator.board.application.comment.port.out.filter.CommentFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record ListUserCommentQuery(
        String userId
) {
    public ListUserCommentQuery {
        Preconditions.requireNonBlank(userId, "userId");
    }

    public static ListUserCommentQuery of(String userId) {
        return new ListUserCommentQuery(userId);
    }

    public CommentFilter toFilter() {
        return CommentFilter.empty()
                .userId(userId);
    }
}
