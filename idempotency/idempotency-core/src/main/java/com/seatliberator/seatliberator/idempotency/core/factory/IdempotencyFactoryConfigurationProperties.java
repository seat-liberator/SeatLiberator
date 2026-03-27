package com.seatliberator.seatliberator.idempotency.core.factory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "idempotency.factory")
public record IdempotencyFactoryConfigurationProperties(
        @DefaultValue(".") String keySeparator
) {
    public IdempotencyFactoryConfigurationProperties {
        if (keySeparator == null || keySeparator.isBlank()) {
            throw new IllegalArgumentException("keySeparator must be not null or blank");
        }
    }
}
