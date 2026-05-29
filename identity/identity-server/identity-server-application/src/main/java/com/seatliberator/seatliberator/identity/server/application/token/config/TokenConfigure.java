package com.seatliberator.seatliberator.identity.server.application.token.config;

import com.seatliberator.seatliberator.identity.server.application.token.internal.Hasher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class TokenConfigure {
    @Bean
    Hasher hasher(TokenProperties properties) {
        var hash = properties.hash();
        return new Hasher(hash.algorithm(), hash.secret());
    }
}
