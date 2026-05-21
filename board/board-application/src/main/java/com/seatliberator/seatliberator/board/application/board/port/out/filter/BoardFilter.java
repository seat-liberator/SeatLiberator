package com.seatliberator.seatliberator.board.application.board.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.jspecify.annotations.Nullable;

public record BoardFilter(
        @Nullable String name,
        @Nullable String description
) {
    public static BoardFilter empty() {
        return new BoardFilter(null, null);
    }

    public BoardFilter name(String name) {
        Preconditions.requireNonBlank(name, "name");

        return new BoardFilter(name, description);
    }

    public BoardFilter description(String description) {
        Preconditions.requireNonBlank(description, "description");

        return new BoardFilter(name, description);
    }
}
