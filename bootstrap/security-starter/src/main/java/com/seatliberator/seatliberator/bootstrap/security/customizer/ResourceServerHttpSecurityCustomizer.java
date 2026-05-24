package com.seatliberator.seatliberator.bootstrap.security.customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface ResourceServerHttpSecurityCustomizer {
    void customize(HttpSecurity http) throws Exception;
}
