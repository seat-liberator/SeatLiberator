package com.seatliberator.seatliberator.board.infrastructure.security;

import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerAuthorizeRequestMatcherCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static com.seatliberator.seatliberator.board.infrastructure.security.BoardCapability.CATEGORY_MANAGE;
import static com.seatliberator.seatliberator.board.infrastructure.security.BoardCapability.POST_CREATE;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    ResourceServerAuthorizeRequestMatcherCustomizer resourceServerAuthorizeCustomizer() {
        return auth -> auth
                .requestMatchers(HttpMethod.POST, "/board/*/categories").hasAuthority(CATEGORY_MANAGE.scope())
                .requestMatchers(HttpMethod.PATCH, "/board/*/categories/*").hasAuthority(CATEGORY_MANAGE.scope())
                .requestMatchers(HttpMethod.DELETE, "/board/*/categories/*").hasAuthority(CATEGORY_MANAGE.scope())
                .requestMatchers(HttpMethod.POST, "/board/*/posts").hasAuthority(POST_CREATE.scope());
    }
}
