package com.seatliberator.seatliberator.board.application.category.port.in.query;

import com.seatliberator.seatliberator.board.application.category.port.out.filter.CategoryFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record ListCategoryQuery(
        UUID boardId,
        @Nullable String name,
        @Nullable String description
) {
    public ListCategoryQuery {
        Preconditions.requireNonNull(boardId, "boardId");
    }

    public static ListCategoryQuery of(UUID boardId, String name, String description) {
        return new ListCategoryQuery(boardId, name, description);
    }

    public CategoryFilter toFilter() {
        return CategoryFilter.empty()
                .name(name)
                .description(description);
    }
}
