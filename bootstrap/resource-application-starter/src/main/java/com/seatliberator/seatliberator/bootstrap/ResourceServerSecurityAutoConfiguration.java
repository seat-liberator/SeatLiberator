package com.seatliberator.seatliberator.bootstrap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@ConditionalOnClass({HttpSecurity.class, SecurityFilterChain.class, JwtDecoder.class})
@ConditionalOnProperty(
        prefix = "seatliberator.bootstrap.resource-server.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ResourceServerSecurityConfigurationProperties.class)
public class ResourceServerSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationTokenConverter.class)
    JwtAuthenticationTokenConverter defaultJwtAuthenticationTokenConverter() {
        return JwtAuthenticationToken::new;
    }

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(
            ResourceServerSecurityConfigurationProperties properties
    ) {
        if (properties.jwkSetUri() == null) {
            throw new IllegalArgumentException(
                    "seatliberator.bootstrap.resource-server.security.jwk-set-uri must not be null when enabled=true."
            );
        }
        return NimbusJwtDecoder.withJwkSetUri(properties.jwkSetUri().toString()).build();
    }

    @Bean
    @ConditionalOnMissingBean
    SecurityFilterChain apiSecurity(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter,
            ResourceServerSecurityConfigurationProperties properties,
            ObjectProvider<List<ResourceServerSecurityCustomizer>> customizersProvider
    ) throws Exception {
        if (!properties.csrfEnabled()) {
            http.csrf(CsrfConfigurer::disable);
        }

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> {
            for (String permit : properties.permits()) {
                auth.requestMatchers(permit).permitAll();
            }
        });

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationConverter)
        ));

        List<ResourceServerSecurityCustomizer> customizers = new ArrayList<>(customizersProvider.getIfAvailable(List::of));
        AnnotationAwareOrderComparator.sort(customizers);

        for (var customizer : customizers) {
            customizer.customize(http);
        }

        return http.build();
    }
}
