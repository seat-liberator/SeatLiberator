package com.seatliberator.seatliberator.board.application.category.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record DeleteCategoryCommand(
        UUID categoryId
) {
    public DeleteCategoryCommand {
        Preconditions.requireNonNull(categoryId, "categoryId");
    }

    public static DeleteCategoryCommand of(UUID categoryId) {
        return new DeleteCategoryCommand(categoryId);
    }
}
