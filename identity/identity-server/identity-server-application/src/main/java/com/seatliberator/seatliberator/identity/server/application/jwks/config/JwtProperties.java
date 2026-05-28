package com.seatliberator.seatliberator.identity.server.application.jwks.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "identity.jwt")
public record JwtProperties(
        @DefaultValue("seatliberator-identity")
        @NotBlank String issuer,

        @DefaultValue({"seatliberator-api"})
        @NotNull List<@NotBlank String> audience,

        @DefaultValue("PT15M")
        @NotNull Duration expiration
) {
}
