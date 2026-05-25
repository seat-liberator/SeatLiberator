package com.seatliberator.seatliberator.board.web.shared.security;

import com.seatliberator.seatliberator.starter.security.customizer.HttpSecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import static com.seatliberator.seatliberator.board.application.shared.config.BoardCapability.CATEGORY_MANAGE;
import static com.seatliberator.seatliberator.board.application.shared.config.BoardCapability.POST_CREATE;

@Configuration
public class SecurityConfiguration {

    @Bean
    HttpSecurityCustomizer authorizeCustomizer() {
        return httpSecurity -> httpSecurity.authorizeHttpRequests(registry -> registry
                .requestMatchers(HttpMethod.POST, "/board/*/categories").hasAuthority(CATEGORY_MANAGE.scope())
                .requestMatchers(HttpMethod.PATCH, "/board/*/categories/*").hasAuthority(CATEGORY_MANAGE.scope())
                .requestMatchers(HttpMethod.DELETE, "/board/*/categories/*").hasAuthority(CATEGORY_MANAGE.scope())
                .requestMatchers(HttpMethod.POST, "/board/*/posts").hasAuthority(POST_CREATE.scope())
        );
    }
}
