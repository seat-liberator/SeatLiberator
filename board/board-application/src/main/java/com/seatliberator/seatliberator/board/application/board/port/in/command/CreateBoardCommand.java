package com.seatliberator.seatliberator.board.application.board.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record CreateBoardCommand(
        String name,
        String description
) {
    public CreateBoardCommand {
        Preconditions.requireNonBlank(name, "name");
        Preconditions.requireNonBlank(description, "description");
    }

    public static CreateBoardCommand of(String name, String description) {
        return new CreateBoardCommand(name, description);
    }
}
