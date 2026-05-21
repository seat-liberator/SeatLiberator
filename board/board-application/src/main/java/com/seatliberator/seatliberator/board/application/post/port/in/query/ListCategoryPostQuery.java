package com.seatliberator.seatliberator.board.application.post.port.in.query;

import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record ListCategoryPostQuery(
        UUID categoryId
) {
    public ListCategoryPostQuery {
        Preconditions.requireNonNull(categoryId, "categoryId");
    }

    public static ListCategoryPostQuery of(UUID categoryId) {
        return new ListCategoryPostQuery(categoryId);
    }

    public PostFilter toFilter() {
        return PostFilter.empty()
                .categoryId(categoryId);
    }
}
