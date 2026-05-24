package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderBasedInitialNamespaceRoleRegistry implements InitialNamespaceRoleRegistry {
    private final Map<String, NamespaceRole> registry;
    private final Role fallbackRole;

    public ProviderBasedInitialNamespaceRoleRegistry(
            List<InitialNamespaceRoleProvider> providers,
            Role fallbackRole
    ) {
        Preconditions.requireNonNull(providers, "providers");
        Preconditions.requireNonNull(fallbackRole, "fallbackRole");

        Map<String, NamespaceRole> initial = new HashMap<>();

        providers.forEach(provider -> {
            var namespaceRole = provider.provide();
            var key = keyOf(namespaceRole.namespace());

            if (initial.putIfAbsent(key, namespaceRole) != null)
                throw new IllegalArgumentException("Duplicated namespace found: " + key);
        });

        this.registry = Map.copyOf(initial);
        this.fallbackRole = fallbackRole;
    }

    @Override
    public Collection<NamespaceRole> getAll() {
        return List.copyOf(registry.values());
    }

    @Override
    public NamespaceRole resolveByNamespace(ApplicationNamespace namespace) {
        Preconditions.requireNonNull(namespace, "namespace");
        return registry.getOrDefault(keyOf(namespace), SimpleNamespaceRole.from(namespace, fallbackRole));
    }

    private String keyOf(ApplicationNamespace namespace) {
        return namespace.value();
    }
}
