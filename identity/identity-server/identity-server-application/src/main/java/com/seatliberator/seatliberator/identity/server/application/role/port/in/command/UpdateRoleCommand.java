package com.seatliberator.seatliberator.identity.server.application.role.port.in.command;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateRoleCommand(
        UUID userId,
        ApplicationNamespace namespace,
        Role role
) {
    public UpdateRoleCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonNull(namespace, "namespace");
        Preconditions.requireNonNull(role, "role");
    }

    public static UpdateRoleCommand of(UUID userId, ApplicationNamespace namespace, Role role) {
        return new UpdateRoleCommand(userId, namespace, role);
    }
}
