package com.seatliberator.seatliberator.identity.server.application.role.internal;

import com.seatliberator.seatliberator.identity.api.DefaultNamespaceRoleGrantProvider;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InitialNamespaceRoleRegistry {
    private final Map<SimpleApplicationNamespace, NamespaceRole> grants;

    public InitialNamespaceRoleRegistry() {
        this.grants = ServiceLoader.load(DefaultNamespaceRoleGrantProvider.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(provider -> provider.grants().stream())
                .collect(Collectors.toUnmodifiableMap(
                        e -> SimpleApplicationNamespace.from(e.namespace()),
                        Function.identity()
                ));
    }

    public List<NamespaceRole> getInitialNamespaceRoles() {
        return List.copyOf(grants.values());
    }
}
