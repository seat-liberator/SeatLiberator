package com.seatliberator.seatliberator.identity.server.application.token.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record RevokeRefreshTokenCommand(String refreshToken) {
    public RevokeRefreshTokenCommand {
        Preconditions.requireNonBlank(refreshToken, "refreshToken");
    }

    public static RevokeRefreshTokenCommand of(String refreshToken) {
        return new RevokeRefreshTokenCommand(refreshToken);
    }
}
