package com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record AuthenticationCredentialCommand(
        String email,
        String password
) {
    public AuthenticationCredentialCommand {
        Preconditions.requireNonBlank(email, "email");
        Preconditions.requireNonBlank(password, "password");
    }

    public static AuthenticationCredentialCommand of(String email, String password) {
        return new AuthenticationCredentialCommand(email, password);
    }
}
