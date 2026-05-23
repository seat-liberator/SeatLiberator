package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SeparatorBasedNamespaceRoleSerializer implements NamespaceRoleSerializer {
    public static final String SEPARATOR = ":";

    @Override
    public String serialize(NamespaceRole namespaceRole) {
        Preconditions.requireNonNull(namespaceRole, "namespaceRole");

        var namespace = namespaceRole.namespace();
        if (namespace.value().contains(SEPARATOR)) {
            throw new IllegalArgumentException(
                    "namespace value must not contain separator '%s': '%s'."
                            .formatted(SEPARATOR, namespace)
            );
        }

        return namespaceRole.namespace().value() + SEPARATOR + namespaceRole.role().name();
    }

    @Override
    public Set<String> serialize(Collection<NamespaceRole> namespaceRoles) {
        if (namespaceRoles == null) throw new IllegalArgumentException("namespaceRoles must not be null.");

        return namespaceRoles.stream()
                .map(this::serialize)
                .collect(Collectors.toUnmodifiableSet());
    }
}
