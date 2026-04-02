package com.seatliberator.seatliberator.bootstrap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@ConfigurationProperties(prefix = "seatliberator.bootstrap.resource-server.security")
@Validated
public record ResourceServerSecurityConfigurationProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("false")
        boolean csrfEnabled,

        URI jwkSetUri,

        @DefaultValue({"/error", "/actuator/health"})
        @NotEmpty
        List<@NotBlank String> permits,

        @DefaultValue("true")
        boolean actorContextEnabled
) {
    public ResourceServerSecurityConfigurationProperties {
        permits = List.copyOf(permits);
    }
}
