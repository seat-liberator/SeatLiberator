package com.seatliberator.seatliberator.board.application.post.port.in.query;

import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record ListBoardPostQuery(
        UUID boardId,
        @Nullable UUID categoryId
) {
    public ListBoardPostQuery {
        Preconditions.requireNonNull(boardId, "boardId");
    }

    public static ListBoardPostQuery of(UUID boardId, UUID categoryId) {
        return new ListBoardPostQuery(boardId, categoryId);
    }

    public PostFilter toFilter() {
        return PostFilter.empty()
                .boardId(boardId)
                .categoryId(categoryId);
    }
}
