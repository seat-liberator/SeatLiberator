package com.seatliberator.seatliberator.identity.server.application.role.service;

import com.seatliberator.seatliberator.identity.api.DefaultNamespaceRoleGrantProvider;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
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
    private final Map<SimpleApplicationNamespace, NamespaceRole> grants;

    public BootstrapDefaultGrantRegistry() {
        this.grants = ServiceLoader.load(DefaultNamespaceRoleGrantProvider.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(provider -> provider.grants().stream())
                .collect(Collectors.toUnmodifiableMap(
                        e -> SimpleApplicationNamespace.from(e.namespace()),
                        Function.identity()
                ));
    }

    public List<NamespaceRole> getDefaultNamespaceRole() {
        return List.copyOf(grants.values());
    }

    public NamespaceRole getDefaultRole(ApplicationNamespace namespace) {
        return grants.getOrDefault(
                SimpleApplicationNamespace.from(namespace),
                SimpleNamespaceRole.from(namespace, Role.GUEST)
        );
    }
}
