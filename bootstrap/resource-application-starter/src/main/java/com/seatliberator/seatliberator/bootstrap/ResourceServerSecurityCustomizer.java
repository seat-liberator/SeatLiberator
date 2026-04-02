package com.seatliberator.seatliberator.bootstrap;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface ResourceServerSecurityCustomizer {
    void customize(HttpSecurity httpSecurity) throws Exception;
}
