package com.seatliberator.seatliberator.identity.application.service;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.role.api.DefaultNamespaceRoleGrant;
import com.seatliberator.seatliberator.role.api.DefaultNamespaceRoleGrantProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BootstrapDefaultGrantRegistry {
    private final Map<String, NamespaceRole> grants;

    public BootstrapDefaultGrantRegistry() {
        this.grants = ServiceLoader.load(DefaultNamespaceRoleGrantProvider.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(provider -> provider.grants().stream())
                .collect(Collectors.toUnmodifiableMap(
                        DefaultNamespaceRoleGrant::namespace,
                        Function.identity()
                ));
    }

    public List<NamespaceRole> getDefaultNamespaceRole() {
        return List.copyOf(grants.values());
    }

    public NamespaceRole getDefaultRole(String namespace) {
        return grants.getOrDefault(namespace, SimpleNamespaceRole.from(namespace, Role.GUEST));
    }
}
