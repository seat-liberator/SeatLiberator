package com.seatliberator.seatliberator.identity.core.role;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SeparatorNamespaceRoleFormatter implements NamespaceRoleFormatter {
    private final String separator;

    public SeparatorNamespaceRoleFormatter(String separator) {
        if (separator == null || separator.isBlank())
            throw new IllegalArgumentException("separator must not be null or blank.");
        this.separator = separator;
    }

    @Override
    public String format(NamespaceRole namespaceRole) {
        if (namespaceRole == null) throw new IllegalArgumentException("namespaceRole must not be null.");

        var namespace = namespaceRole.namespace().value();

        if (namespace.contains(separator)) {
            throw new IllegalArgumentException(
                    "namespace value must not contain separator '%s': '%s'."
                            .formatted(separator, namespace)
            );
        }

        return namespaceRole.namespace().value() + separator + namespaceRole.role().name();
    }

    @Override
    public Set<String> format(Collection<NamespaceRole> namespaceRoles) {
        if (namespaceRoles == null) throw new IllegalArgumentException("namespaceRoles must not be null.");

        return namespaceRoles.stream()
                .map(this::format)
                .collect(Collectors.toUnmodifiableSet());
    }
}
