package com.seatliberator.seatliberator.identity.server.application.token.config;

import com.seatliberator.seatliberator.identity.server.application.token.internal.ByteEncoder;
import com.seatliberator.seatliberator.identity.server.application.token.internal.Hasher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class TokenConfigure {
    @Bean
    Hasher hasher(TokenProperties properties) {
        var hash = properties.hash();
        return new Hasher(hash.algorithm(), hash.secret());
    }

    @Bean
    ByteEncoder byteEncoder() {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder::encodeToString;
    }
}
