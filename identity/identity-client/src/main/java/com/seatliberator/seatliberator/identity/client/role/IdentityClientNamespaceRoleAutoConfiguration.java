package com.seatliberator.seatliberator.identity.client.role;

import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
public class IdentityClientNamespaceRoleAutoConfiguration {

    @Bean
    NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry(
            CurrentApplicationNamespaceProvider namespaceProvider,
            List<RoleCapabilities> roleCapabilities
    ) {
        return new NamespaceRoleCapabilitiesRegistry(namespaceProvider.current(), roleCapabilities);
    }
}
