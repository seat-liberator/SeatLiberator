package com.seatliberator.seatliberator.identity.server.domain.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddableNamespaceRole implements NamespaceRole {
    private String namespace;

    @Enumerated(EnumType.STRING)
    private Role role;

    private EmbeddableNamespaceRole(String namespace, Role role) {
        this.namespace = Preconditions.requireNonBlank(namespace, "namespace");
        this.role = Preconditions.requireNonNull(role, "role");
    }

    public static EmbeddableNamespaceRole of(ApplicationNamespace namespace, Role role) {
        Preconditions.requireNonNull(namespace, "namespace");

        return new EmbeddableNamespaceRole(namespace.value(), role);
    }

    public static EmbeddableNamespaceRole from(NamespaceRole namespaceRole) {
        Preconditions.requireNonNull(namespaceRole, "namespaceRole");

        return new EmbeddableNamespaceRole(namespaceRole.namespace().value(), namespaceRole.role());
    }

    @Override
    public ApplicationNamespace namespace() {
        return SimpleApplicationNamespace.of(namespace);
    }

    @Override
    public Role role() {
        return role;
    }

    public EmbeddableNamespaceRole withRole(Role role) {
        return new EmbeddableNamespaceRole(namespace, role);
    }
}
