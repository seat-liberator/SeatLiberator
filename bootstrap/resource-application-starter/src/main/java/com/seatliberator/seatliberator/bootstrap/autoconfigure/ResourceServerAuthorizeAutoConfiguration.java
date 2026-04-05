package com.seatliberator.seatliberator.bootstrap.autoconfigure;

import com.seatliberator.seatliberator.bootstrap.security.ActorContextBindingFilter;
import com.seatliberator.seatliberator.bootstrap.security.ActorContextJwtAuthenticationConverter;
import com.seatliberator.seatliberator.bootstrap.security.JwtAuthenticationTokenConverter;
import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerAuthorizeRequestMatcherCustomizer;
import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerHttpSecurityCustomizer;
import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerOAuth2Customizer;
import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.client.actor.ThreadLocalActorContextHolder;
import com.seatliberator.seatliberator.identity.client.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "seatliberator.resource-server.security.authorize",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ResourceServerAuthorizeProperties.class)
public class ResourceServerAuthorizeAutoConfiguration {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    ResourceServerOAuth2Customizer defaultResourceServerOAuth2Customizer(
            JwtDecoder jwtDecoder,
            JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter
    ) {
        return oauth -> oauth.jwt(jwt -> jwt
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationTokenConverter)
        );
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    ResourceServerAuthorizeRequestMatcherCustomizer defaultResourceServerAuthorizeRequestMatcherCustomizer(
            ResourceServerAuthorizeProperties properties
    ) {
        return auth -> {
            for (var permit : properties.permits()) auth.requestMatchers(permit).permitAll();
        };
    }

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(ResourceServerAuthorizeProperties properties) {
        if (properties.jwkSetUri() == null) {
            throw new IllegalArgumentException(
                    "seatliberator.resource-server.security.jwk-set-uri must not be null when enabled=true."
            );
        }
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
    @ConditionalOnMissingBean
    ActorContextBindingFilter actorContextBindingFilter(ActorContextHolder actorContextHolder) {
        return new ActorContextBindingFilter(actorContextHolder);
    }

    @Bean
    ResourceServerHttpSecurityCustomizer oauth2SecurityCustomizer(
            ActorContextBindingFilter actorContextBindingFilter,
            ObjectProvider<List<ResourceServerOAuth2Customizer>> oauth2CustomizerProvider
    ) {
        return http -> http
                .oauth2ResourceServer(oauth2 -> {
                    var customizers = new ArrayList<>(oauth2CustomizerProvider.getIfAvailable(ArrayList::new));
                    AnnotationAwareOrderComparator.sort(customizers);
                    for (var customizer : customizers) customizer.customize(oauth2);
                })
                .addFilterAfter(actorContextBindingFilter, BearerTokenAuthenticationFilter.class);
    }

    @Bean
    ResourceServerHttpSecurityCustomizer authorizeHttpRequestSecurityCustomizer(
            ObjectProvider<List<ResourceServerAuthorizeRequestMatcherCustomizer>> authorizeCustomizerProvider,
            ResourceServerAuthorizeProperties authorizeProperties
    ) {
        return http -> http
                .authorizeHttpRequests(auth -> {
                    var customizers = new ArrayList<>(authorizeCustomizerProvider.getIfAvailable(ArrayList::new));
                    AnnotationAwareOrderComparator.sort(customizers);
                    for (var customizer : customizers) customizer.customize(auth);

                    switch (authorizeProperties.requireAuthorizeAny()) {
                        case ALL -> auth.anyRequest().authenticated();
                        case NEVER -> auth.anyRequest().permitAll();
                    }
                });
    }
}
