package com.seatliberator.seatliberator.bootstrap.security.customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@FunctionalInterface
public interface ResourceServerAuthorizeRequestMatcherCustomizer {
    void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>
                           .AuthorizationManagerRequestMatcherRegistry auth);
}
