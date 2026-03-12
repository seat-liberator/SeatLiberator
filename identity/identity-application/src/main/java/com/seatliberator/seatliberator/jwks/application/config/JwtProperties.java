package com.seatliberator.seatliberator.jwks.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "identity.jwt")
public record JwtProperties(
        Duration expiration
) {
    public JwtProperties {
        expiration = expiration != null ? expiration : Duration.ofMillis(900000);
    }
}
