package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.domain.Category;

import java.util.UUID;

public record CategoryEntry(
        UUID categoryId,
        UUID boardId,
        String name,
        String description
) {
    public static CategoryEntry of(Category category) {
        return new CategoryEntry(
                category.getId(),
                category.getBoard().getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
