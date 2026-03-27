package com.seatliberator.seatliberator.idempotency.core.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "idempotency.store")
public record IdempotencyStoreConfigurationProperties(
        @DefaultValue("3") int maxAttemptCount,
        @DefaultValue("1000") Duration timeout
) {
    public IdempotencyStoreConfigurationProperties {
        if (maxAttemptCount < 1) {
            throw new IllegalArgumentException("maxAttemptCount must not be less than 1");
        }
        if (timeout == null || !timeout().isPositive()) {
            throw new IllegalArgumentException("timeout duration must not be null or negative value");
        }
    }
}
