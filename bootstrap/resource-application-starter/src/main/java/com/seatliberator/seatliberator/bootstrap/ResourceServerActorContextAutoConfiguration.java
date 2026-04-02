package com.seatliberator.seatliberator.bootstrap;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.client.actor.ThreadLocalActorContextHolder;
import com.seatliberator.seatliberator.identity.client.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "seatliberator.bootstrap.resource-server.security",
        name = "actorContextEnabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ResourceServerActorContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ActorContextJwtAuthenticationConverter actorContextJwtAuthenticationConverter(
            NamespaceRoleDeserializer namespaceRoleDeserializer,
            NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry
    ) {
        return new ActorContextJwtAuthenticationConverter(namespaceRoleDeserializer, namespaceRoleCapabilitiesRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(ActorContextHolder.class)
    ThreadLocalActorContextHolder threadLocalActorContextHolder() {
        return new ThreadLocalActorContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean
    ActorContextBindingFilter actorContextBindingFilter(
            ActorContextHolder actorContextHolder
    ) {
        return new ActorContextBindingFilter(actorContextHolder);
    }

    @Bean
    ResourceServerSecurityCustomizer actorContextCustomizer(
            ActorContextBindingFilter actorContextBindingFilter
    ) {
        return http -> http.addFilterAfter(actorContextBindingFilter, BearerTokenAuthenticationFilter.class);
    }
}
