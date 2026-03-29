package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;

public record SimpleNamespaceRole(
        ApplicationNamespace namespace,
        Role role
) implements NamespaceRole {
    public SimpleNamespaceRole {
        if (namespace == null) {
            throw new IllegalArgumentException("namespace must not be null.");
        }

        if (role == null) {
            throw new IllegalArgumentException("role must not be null.");
        }
    }

    public static SimpleNamespaceRole from(ApplicationNamespace namespace, Role role) {
        return new SimpleNamespaceRole(namespace, role);
    }

    public static SimpleNamespaceRole copyOf(NamespaceRole namespaceRole) {
        return new SimpleNamespaceRole(namespaceRole.namespace(), namespaceRole.role());
    }
}
