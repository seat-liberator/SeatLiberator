package com.seatliberator.seatliberator.identity.client.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NamespaceRoleCapabilitiesRegistry {
    private final Map<SimpleNamespaceRole, Set<Capability>> registry;

    public NamespaceRoleCapabilitiesRegistry(String namespace, List<RoleCapabilities> roleCapabilitiesList) {
        this.registry = new HashMap<>();

        for (var roleCapabilities : roleCapabilitiesList) {
            var role = roleCapabilities.role();
            var capabilities = roleCapabilities.capabilities();
            var namespaceRole = SimpleNamespaceRole.from(namespace, role);
            var previous = registry.putIfAbsent(namespaceRole, capabilities);
            if (previous != null) throw new IllegalStateException(String.format(
                    "Duplicated role capabilities. role=%s", role.name()
            ));
        }
    }

    public Set<Capability> resolve(NamespaceRole namespaceRole) {
        return registry.getOrDefault(SimpleNamespaceRole.copyOf(namespaceRole), Set.of());
    }

    public Set<Capability> resolve(String namespace, Role role) {
        var namespaceRole = SimpleNamespaceRole.from(namespace, role);
        return registry.getOrDefault(namespaceRole, Set.of());
    }
}
