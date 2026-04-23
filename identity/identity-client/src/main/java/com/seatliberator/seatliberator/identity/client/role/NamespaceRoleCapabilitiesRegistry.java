package com.seatliberator.seatliberator.identity.client.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NamespaceRoleCapabilitiesRegistry {
    private final Map<SimpleNamespaceRole, Set<Capability>> registry;

    public NamespaceRoleCapabilitiesRegistry(ApplicationNamespace namespace, List<RoleCapabilities> roleCapabilitiesList) {
        this.registry = new HashMap<>();
        var directCapabilitiesByRole = new EnumMap<Role, Set<Capability>>(Role.class);

        for (var roleCapabilities : roleCapabilitiesList) {
            var role = roleCapabilities.role();
            var capabilities = roleCapabilities.capabilities();
            var previous = directCapabilitiesByRole.putIfAbsent(role, capabilities);
            if (previous != null) throw new IllegalStateException(String.format(
                    "Duplicated role capabilities. role=%s", role.name()
            ));
        }

        var inheritedCapabilities = new LinkedHashSet<Capability>();
        for (var role : Role.values()) {
            inheritedCapabilities.addAll(directCapabilitiesByRole.getOrDefault(role, Set.of()));
            var namespaceRole = SimpleNamespaceRole.from(namespace, role);
            registry.put(namespaceRole, Set.copyOf(inheritedCapabilities));
        }
    }

    public Set<Capability> resolve(NamespaceRole namespaceRole) {
        return registry.getOrDefault(SimpleNamespaceRole.copyOf(namespaceRole), Set.of());
    }

    public Set<Capability> resolve(ApplicationNamespace namespace, Role role) {
        var namespaceRole = SimpleNamespaceRole.from(namespace, role);
        return registry.getOrDefault(namespaceRole, Set.of());
    }
}
