package com.seatliberator.seatliberator.notification.infrastructure.security;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.client.actor.ThreadLocalActorContextHolder;
import com.seatliberator.seatliberator.identity.client.jwt.ActorContextJwtAuthenticationConverter;
import com.seatliberator.seatliberator.identity.client.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    ThreadLocalActorContextHolder threadLocalActorContextHolder() {
        return new ThreadLocalActorContextHolder();
    }

    @Bean
    ActorContextJwtAuthenticationConverter jwtAuthenticationConverter(
            NamespaceRoleDeserializer namespaceRoleDeserializer,
            NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry
    ) {
        return new ActorContextJwtAuthenticationConverter(namespaceRoleDeserializer, namespaceRoleCapabilitiesRegistry);
    }

    @Bean
    SecurityFilterChain apiSecurity(
            HttpSecurity httpSecurity,
            JwtDecoder jwtDecoder,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter,
            ActorContextHolder actorContextHolder
    ) throws Exception {
        httpSecurity
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .addFilterAfter(
                        new ActorContextBindingFilter(actorContextHolder),
                        BearerTokenAuthenticationFilter.class
                );

        return httpSecurity.build();
    }
}
