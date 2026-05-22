package com.seatliberator.seatliberator.identity.server.application.credential.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdatePasswordCommand(
        UUID userId,
        String oldPassword,
        String newPassword
) {
    public UpdatePasswordCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonBlank(oldPassword, "oldPassword");
        Preconditions.requireNonBlank(newPassword, "newPassword");
    }

    public static UpdatePasswordCommand of(UUID userId, String oldPassword, String newPassword) {
        return new UpdatePasswordCommand(userId, oldPassword, newPassword);
    }
}
