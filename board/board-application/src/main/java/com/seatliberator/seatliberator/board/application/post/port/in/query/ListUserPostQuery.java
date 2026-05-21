package com.seatliberator.seatliberator.board.application.post.port.in.query;

import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record ListUserPostQuery(
        String userId
) {
    public ListUserPostQuery {
        Preconditions.requireNonNull(userId, "userId");
    }

    public static ListUserPostQuery of(String userId) {
        return new ListUserPostQuery(userId);
    }

    public PostFilter toFilter() {
        return PostFilter.empty()
                .userId(userId);
    }
}
