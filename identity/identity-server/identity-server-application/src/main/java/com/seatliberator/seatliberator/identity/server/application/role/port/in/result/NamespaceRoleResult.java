package com.seatliberator.seatliberator.identity.server.application.role.port.in.result;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record NamespaceRoleResult(
        String namespace,
        Role role
) {
    public NamespaceRoleResult {
        Preconditions.requireNonNull(namespace, "namespace");
        Preconditions.requireNonNull(role, "role");
    }

    public static NamespaceRoleResult from(NamespaceRole namespaceRole) {
        return new NamespaceRoleResult(namespaceRole.namespace().value(), namespaceRole.role());
    }
}
