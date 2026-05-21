package com.seatliberator.seatliberator.board.application.category.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindCategoryQuery(
        UUID categoryId
) {
    public FindCategoryQuery {
        Preconditions.requireNonNull(categoryId, "categoryId");
    }

    public static FindCategoryQuery of(UUID categoryId) {
        return new FindCategoryQuery(categoryId);
    }
}
