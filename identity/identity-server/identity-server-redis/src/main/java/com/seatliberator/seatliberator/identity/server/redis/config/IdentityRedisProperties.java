package com.seatliberator.seatliberator.identity.server.redis.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "identity.redis")
public record IdentityRedisProperties(
        @DefaultValue("identity")
        @NotBlank String keyPrefix
) {
}
