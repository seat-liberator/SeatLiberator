package com.seatliberator.seatliberator.board.application.category.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateCategoryNameCommand(
        UUID categoryId,
        String name
) {
    public UpdateCategoryNameCommand {
        Preconditions.requireNonNull(categoryId, "categoryId");
        Preconditions.requireNonBlank(name, "name");
    }

    public static UpdateCategoryNameCommand of(UUID boardId, String name) {
        return new UpdateCategoryNameCommand(boardId, name);
    }
}
