package com.seatliberator.seatliberator.identity.server.application.role.port.in.command;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record RevokeRoleCommand(
        UUID userId,
        ApplicationNamespace namespace
) {
    public RevokeRoleCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonNull(namespace, "namespace");
    }

    public static RevokeRoleCommand of(UUID userId, ApplicationNamespace namespace) {
        return new RevokeRoleCommand(userId, namespace);
    }
}
