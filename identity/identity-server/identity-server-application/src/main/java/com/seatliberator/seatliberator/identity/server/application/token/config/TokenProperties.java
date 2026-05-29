package com.seatliberator.seatliberator.identity.server.application.token.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "identity.token")
public record TokenProperties(
        @Valid
        @DefaultValue
        @NotNull
        HashProperties hash,

        @Valid
        @DefaultValue
        @NotNull
        AccessTokenProperties accessToken,

        @Valid
        @DefaultValue
        @NotNull
        RefreshTokenProperties refreshToken
) {
    public record HashProperties(
            @DefaultValue("HmacSHA256")
            @NotBlank String algorithm,

            @NotBlank String secret
    ) {

    }

    public record AccessTokenProperties(
            @DefaultValue("seatliberator-identity")
            @NotBlank String issuer,

            @DefaultValue({"seatliberator-api"})
            @NotNull List<@NotBlank String> audience,

            @DefaultValue("PT15M")
            @NotNull Duration ttl
    ) {
    }

    public record RefreshTokenProperties(
            @DefaultValue("P7D")
            @NotNull Duration sessionTtl,

            @DefaultValue("P3D")
            @NotNull Duration idleTtl
    ) {
    }
}
