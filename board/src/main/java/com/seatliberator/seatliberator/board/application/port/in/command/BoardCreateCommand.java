package com.seatliberator.seatliberator.board.application.port.in.command;

public record BoardCreateCommand(
        String name,
        String description
) {
}
