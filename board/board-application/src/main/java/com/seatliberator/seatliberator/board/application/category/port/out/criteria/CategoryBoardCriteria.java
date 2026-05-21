package com.seatliberator.seatliberator.board.application.category.port.out.criteria;

import com.seatliberator.seatliberator.board.application.category.port.out.filter.CategoryFilter;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CategoryBoardCriteria(
        UUID boardId,
        CategoryFilter filter
) {
    public CategoryBoardCriteria {
        Preconditions.requireNonNull(boardId, "boardId");
        Preconditions.requireNonNull(filter, "filter");
    }

    public static CategoryBoardCriteria of(UUID boardId, CategoryFilter filter) {
        return new CategoryBoardCriteria(boardId, filter);
    }
}
