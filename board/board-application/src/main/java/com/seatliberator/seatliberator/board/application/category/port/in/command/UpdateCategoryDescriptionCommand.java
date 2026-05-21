package com.seatliberator.seatliberator.board.application.category.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateCategoryDescriptionCommand(
        UUID categoryId,
        String description
) {
    public UpdateCategoryDescriptionCommand {
        Preconditions.requireNonNull(categoryId, "categoryId");
        Preconditions.requireNonBlank(description, "description");
    }

    public static UpdateCategoryDescriptionCommand of(UUID boardId, String description) {
        return new UpdateCategoryDescriptionCommand(boardId, description);
    }
}
