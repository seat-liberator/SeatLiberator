package com.seatliberator.seatliberator.notification.infrastructure.security;

import com.seatliberator.seatliberator.bootstrap.ResourceServerSecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {

    @Bean
    ResourceServerSecurityCustomizer resourceServerSecurityCustomizer() {
        return http -> http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    }
}
