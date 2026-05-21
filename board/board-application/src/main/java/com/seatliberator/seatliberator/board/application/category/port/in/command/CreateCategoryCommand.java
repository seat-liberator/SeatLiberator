package com.seatliberator.seatliberator.board.application.category.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CreateCategoryCommand(
        UUID boardId,
        String name,
        String description
) {
    public CreateCategoryCommand {
        Preconditions.requireNonNull(boardId, "boardId");
        Preconditions.requireNonBlank(name, "name");
        Preconditions.requireNonBlank(description, "description");
    }

    public static CreateCategoryCommand of(UUID boardId, String name, String description) {
        return new CreateCategoryCommand(boardId, name, description);
    }
}
