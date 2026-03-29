package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;

import java.util.Arrays;
import java.util.Optional;

public class SeparatorNamespaceRoleDeserializer implements NamespaceRoleDeserializer {
    private final String separator;

    public SeparatorNamespaceRoleDeserializer(String separator) {
        this.separator = separator;
    }

    @Override
    public Optional<NamespaceRole> tryMaterialize(String raw) {
        var parts = raw.split(separator);
        if (parts.length != 2 || Arrays.stream(parts).anyMatch(String::isBlank)) {
            return Optional.empty();
        }

        try {
            var namespace = SimpleApplicationNamespace.of(parts[0]);
            var role = Role.valueOf(parts[1]);
            return Optional.of(SimpleNamespaceRole.from(namespace, role));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
