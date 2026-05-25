package com.seatliberator.seatliberator.identity.starter.security;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.role.*;
import com.seatliberator.seatliberator.identity.starter.security.jwt.ActorContextBindingFilter;
import com.seatliberator.seatliberator.identity.starter.security.jwt.ActorContextJwtAuthenticationConverter;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "identity.starter.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(IdentityClientProperties.class)
public class IdentitySecurityStarterAutoConfigure {
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(IdentityClientProperties properties) {
        return NimbusJwtDecoder.withJwkSetUri(properties.jwkSetUri().toString()).build();
    }

    @Bean
    @ConditionalOnMissingBean(ActorContextJwtAuthenticationConverter.class)
    ActorContextJwtAuthenticationConverter actorContextJwtAuthenticationConverter(
            NamespaceRoleDeserializer namespaceRoleDeserializer,
            NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry,
            CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider
    ) {
        return new ActorContextJwtAuthenticationConverter(namespaceRoleDeserializer, namespaceRoleCapabilitiesRegistry, currentApplicationNamespaceProvider);
    }

    @Bean
    @ConditionalOnMissingBean(ActorContextBindingFilter.class)
    @ConditionalOnBean(ActorContextHolder.class)
    @ConditionalOnClass({
            jakarta.servlet.Filter.class,
            org.springframework.security.core.context.SecurityContextHolder.class,
            org.springframework.web.filter.OncePerRequestFilter.class
    })
    ActorContextBindingFilter actorContextBindingFilter(ActorContextHolder actorContextHolder) {
        return new ActorContextBindingFilter(actorContextHolder);
    }

    @Bean
    @ConditionalOnMissingBean(NamespaceRoleCapabilitiesRegistry.class)
    NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry(
            CurrentApplicationNamespaceProvider namespaceProvider,
            List<RoleCapabilities> roleCapabilities
    ) {
        return new NamespaceRoleCapabilitiesRegistry(namespaceProvider.current(), roleCapabilities);
    }

    @Bean
    @ConditionalOnMissingBean(NamespaceRoleSerializer.class)
    SeparatorBasedNamespaceRoleSerializer separatorBasedNamespaceRoleSerializer() {
        return new SeparatorBasedNamespaceRoleSerializer();
    }

    @Bean
    @ConditionalOnMissingBean(NamespaceRoleDeserializer.class)
    SeparatorBasedNamespaceRoleDeserializer separatorBasedNamespaceRoleDeserializer() {
        return new SeparatorBasedNamespaceRoleDeserializer();
    }
}
