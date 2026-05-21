package com.seatliberator.seatliberator.board.application.post.port.in.query;

import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record ListPostQuery(
        UUID boardId,
        @Nullable UUID categoryId,
        @Nullable String userId
) {
    public ListPostQuery {
        Preconditions.requireNonNull(boardId, "boardId");
    }

    public static ListPostQuery of(UUID boardId, UUID categoryId, String userId) {
        return new ListPostQuery(boardId, categoryId, userId);
    }

    public PostFilter toFilter() {
        return PostFilter.empty()
                .boardId(boardId)
                .categoryId(categoryId)
                .userId(userId);
    }
}
