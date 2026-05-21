package com.seatliberator.seatliberator.board.application.board.port.in.query;

import com.seatliberator.seatliberator.board.application.board.port.out.filter.BoardFilter;
import org.jspecify.annotations.Nullable;

public record ListBoardQuery(
        @Nullable String name,
        @Nullable String description
) {
    public static ListBoardQuery of(String name, String description) {
        return new ListBoardQuery(name, description);
    }

    public BoardFilter toFilter() {
        return BoardFilter.empty()
                .name(name)
                .description(description);
    }
}
