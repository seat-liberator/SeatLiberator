package com.seatliberator.seatliberator.identity.client.autoconfigure;

import com.seatliberator.seatliberator.identity.client.jwt.ActorContextJwtAuthenticationConverter;
import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.context.ThreadLocalActorContextHolder;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import com.seatliberator.seatliberator.identity.core.role.RoleCapabilities;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(IdentityClientProperties.class)
public class IdentityClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(IdentityClientProperties properties) {
        return NimbusJwtDecoder.withJwkSetUri(properties.jwkSetUri().toString()).build();
    }

    @Bean
    @ConditionalOnMissingBean
    ActorContextJwtAuthenticationConverter actorContextJwtAuthenticationConverter(
            NamespaceRoleDeserializer namespaceRoleDeserializer,
            NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry,
            CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider
    ) {
        return new ActorContextJwtAuthenticationConverter(namespaceRoleDeserializer, namespaceRoleCapabilitiesRegistry, currentApplicationNamespaceProvider);
    }

    @Bean
    @ConditionalOnMissingBean(ActorContextHolder.class)
    ThreadLocalActorContextHolder threadLocalActorContextHolder() {
        return new ThreadLocalActorContextHolder();
    }

    @Bean
    NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry(
            CurrentApplicationNamespaceProvider namespaceProvider,
            List<RoleCapabilities> roleCapabilities
    ) {
        return new NamespaceRoleCapabilitiesRegistry(namespaceProvider.current(), roleCapabilities);
    }
}
