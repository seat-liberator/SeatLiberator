package com.seatliberator.seatliberator.board.infrastructure.security;

import com.seatliberator.seatliberator.identity.client.jwt.ActorContextJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import static com.seatliberator.seatliberator.board.infrastructure.security.BoardAuthorities.CATEGORY_MANAGE;
import static com.seatliberator.seatliberator.board.infrastructure.security.BoardAuthorities.POST_CREATE;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new ActorContextJwtAuthenticationConverter();
    }

    @Bean
    SecurityFilterChain apiSecurity(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter
    ) {
        http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/board/*/categories").hasAuthority(CATEGORY_MANAGE)
                        .requestMatchers(HttpMethod.PATCH, "/board/*/categories/*").hasAuthority(CATEGORY_MANAGE)
                        .requestMatchers(HttpMethod.DELETE, "/board/*/categories/*").hasAuthority(CATEGORY_MANAGE)
                        .requestMatchers(HttpMethod.POST, "/board/*/posts").hasAuthority(POST_CREATE)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        try {
            return http.build();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to build security filter chain", exception);
        }
    }
}
