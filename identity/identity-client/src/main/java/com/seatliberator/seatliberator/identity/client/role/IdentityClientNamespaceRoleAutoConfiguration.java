package com.seatliberator.seatliberator.identity.client.role;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
public class IdentityClientNamespaceRoleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(NamespaceProvider.class)
    NamespaceProvider namespaceProvider() {
        return new NamespaceProvider("unknown-namespace");
    }

    @Bean
    NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry(
            NamespaceProvider namespaceProvider,
            List<RoleCapabilities> roleCapabilities
    ) {
        return new NamespaceRoleCapabilitiesRegistry(namespaceProvider.namespace(), roleCapabilities);
    }
}
