package com.seatliberator.seatliberator.bootstrap.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "seatliberator.bootstrap.security")
public record SecurityStarterProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("false")
        boolean csrfEnabled,

        @Valid
        @DefaultValue
        @NotNull
        Cors cors,

        @Valid
        @DefaultValue
        @NotNull
        Authorize authorize
) {
    public record Cors(
            @DefaultValue("true")
            boolean enabled,

            @DefaultValue
            List<@NotBlank String> allowedOrigins,

            @DefaultValue({"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"})
            @NotEmpty
            List<@NotBlank String> allowedMethods,

            @DefaultValue({"Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "X-CSRF-TOKEN"})
            @NotEmpty
            List<@NotBlank String> allowedHeaders,

            @DefaultValue("false")
            boolean allowCredentials,

            @DefaultValue
            List<@NotBlank String> exposedHeaders
    ) {
    }

    public record Authorize(
            @DefaultValue("true")
            boolean enabled,

            @DefaultValue({"/error", "/actuator/prometheus", "/actuator/health", "/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"})
            @NotEmpty
            List<@NotBlank String> permits,

            @DefaultValue("AUTHENTICATED")
            AnyRequestPolicy anyRequest
    ) {
        public enum AnyRequestPolicy {
            AUTHENTICATED,
            DENY_ALL
        }
    }
}
