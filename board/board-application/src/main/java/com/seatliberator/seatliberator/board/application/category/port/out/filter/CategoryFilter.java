package com.seatliberator.seatliberator.board.application.category.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

public record CategoryFilter(
        @Nullable String name,
        @Nullable String description
) {
    public static CategoryFilter empty() {
        return new CategoryFilter(null, null);
    }

    public CategoryFilter name(String name) {
        return new CategoryFilter(name, description);
    }

    public CategoryFilter description(String description) {
        return new CategoryFilter(name, description);
    }
}
