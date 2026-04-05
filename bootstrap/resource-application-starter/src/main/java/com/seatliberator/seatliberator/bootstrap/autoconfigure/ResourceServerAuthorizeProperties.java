package com.seatliberator.seatliberator.bootstrap.autoconfigure;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "seatliberator.resource-server.security.authorize")
public record ResourceServerAuthorizeProperties(
        @DefaultValue("true")
        boolean enabled,

        URI jwkSetUri,

        @DefaultValue({"/error", "/actuator/health", "/actuator/health/**"})
        @NotEmpty
        List<@NotBlank String> permits,

        @DefaultValue("ALL")
        AuthorizeMode requireAuthorizeAny
) {
    public enum AuthorizeMode {
        NEVER,
        ALL
    }
}