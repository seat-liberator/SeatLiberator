package com.seatliberator.seatliberator.board.application.category.port.in.result;

import com.seatliberator.seatliberator.board.domain.Category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResult(
        UUID categoryId,
        UUID boardId,
        String name,
        String description,
        Instant createdAt
) {
    public static CategoryResult from(Category category) {
        return new CategoryResult(
                category.getId(),
                category.getBoardId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }
}
