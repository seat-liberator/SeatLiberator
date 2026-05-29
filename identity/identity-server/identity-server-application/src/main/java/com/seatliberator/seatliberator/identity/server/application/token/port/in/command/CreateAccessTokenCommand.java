package com.seatliberator.seatliberator.identity.server.application.token.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CreateAccessTokenCommand(UUID userId) {
    public CreateAccessTokenCommand {
        Preconditions.requireNonNull(userId, "userId");
    }

    public static CreateAccessTokenCommand of(UUID userId) {
        return new CreateAccessTokenCommand(userId);
    }
}
