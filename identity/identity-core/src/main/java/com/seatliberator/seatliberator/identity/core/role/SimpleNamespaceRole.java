package com.seatliberator.seatliberator.identity.core.role;

public record SimpleNamespaceRole(
        String namespace,
        Role role
) implements NamespaceRole {
    public SimpleNamespaceRole {
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalArgumentException("namespace must not be null or blank.");
        }

        if (role == null) {
            throw new IllegalArgumentException("role must not be null.");
        }
    }

    public static SimpleNamespaceRole from(String namespace, Role role) {
        return new SimpleNamespaceRole(namespace, role);
    }

    public static SimpleNamespaceRole copyOf(NamespaceRole namespaceRole) {
        return new SimpleNamespaceRole(namespaceRole.namespace(), namespaceRole.role());
    }
}
