package com.seatliberator.seatliberator.identity.server.application.shared.config;

import com.seatliberator.seatliberator.identity.api.IdentityApi;
import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleProvider;
import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleRegistry;
import com.seatliberator.seatliberator.identity.core.role.ProviderBasedInitialNamespaceRoleRegistry;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ServiceLoader;

@Configuration
public class IdentityApplicationConfiguration {
    @Bean
    FixedCurrentApplicationNamespaceProvider fixedCurrentApplicationNamespaceProvider() {
        return new FixedCurrentApplicationNamespaceProvider(IdentityApi.NAMESPACE);
    }

    @Bean
    InitialNamespaceRoleRegistry initialNamespaceRoleRegistry() {
        var providers = ServiceLoader.load(InitialNamespaceRoleProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList();

        return new ProviderBasedInitialNamespaceRoleRegistry(providers, Role.GUEST);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
