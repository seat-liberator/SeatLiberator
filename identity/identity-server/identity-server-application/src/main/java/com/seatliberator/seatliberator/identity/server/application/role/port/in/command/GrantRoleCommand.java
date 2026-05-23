package com.seatliberator.seatliberator.identity.server.application.role.port.in.command;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record GrantRoleCommand(
        UUID userId,
        NamespaceRole namespaceRole
) {
    public GrantRoleCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonNull(namespaceRole, "namespaceRole");
    }

    public static GrantRoleCommand of(UUID userId, NamespaceRole namespaceRole) {
        return new GrantRoleCommand(userId, namespaceRole);
    }
}
