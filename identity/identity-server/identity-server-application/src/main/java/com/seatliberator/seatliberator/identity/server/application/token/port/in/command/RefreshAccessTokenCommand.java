package com.seatliberator.seatliberator.identity.server.application.token.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record RefreshAccessTokenCommand(String refreshToken) {
    public RefreshAccessTokenCommand {
        Preconditions.requireNonBlank(refreshToken, "refreshToken");
    }

    public static RefreshAccessTokenCommand of(String refreshToken) {
        return new RefreshAccessTokenCommand(refreshToken);
    }
}
