package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SeparatorBasedNamespaceRoleDeserializer implements NamespaceRoleDeserializer {
    public static final String SEPARATOR = ":";

    @Override
    public NamespaceRole materialize(String raw) {
        Preconditions.requireNonBlank(raw, "raw");

        var parts = raw.split(Pattern.quote(SEPARATOR), -1);
        if (parts.length != 2 || Arrays.stream(parts).anyMatch(String::isBlank))
            throw new IllegalArgumentException("invalid namespace role format. expected '<namespace>%s<role>', but was '%s'.".formatted(SEPARATOR, raw));

        var namespace = SimpleApplicationNamespace.of(parts[0]);

        try {
            var role = Role.valueOf(parts[1]);
            return SimpleNamespaceRole.from(namespace, role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "invalid role '%s' in namespace role '%s'. expected one of: %s."
                            .formatted(parts[1], raw, Arrays.toString(Role.values())),
                    e
            );
        }
    }

    @Override
    public Set<NamespaceRole> materialize(Collection<String> rawCollection) {
        if (rawCollection == null) {
            throw new IllegalArgumentException("rawCollection must not be null.");
        }

        return rawCollection.stream()
                .map(this::materialize)
                .collect(Collectors.toUnmodifiableSet());
    }
}
