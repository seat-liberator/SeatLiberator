package com.seatliberator.seatliberator.identity.server.application.credential.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record RegisterCredentialAccountCommand(
        String nickname,
        String email,
        String password
) {
    public RegisterCredentialAccountCommand {
        Preconditions.requireNonBlank(nickname, "nickname");
        Preconditions.requireNonBlank(email, "email");
        Preconditions.requireNonBlank(password, "password");
    }

    public static RegisterCredentialAccountCommand of(String nickname, String email, String password) {
        return new RegisterCredentialAccountCommand(nickname, email, password);
    }
}
