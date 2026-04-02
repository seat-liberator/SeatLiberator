package com.seatliberator.seatliberator.bootstrap.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "seatliberator.resource-server.security")
public record ResourceServerSecurityProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("false")
        boolean csrfEnabled,

        @DefaultValue("true")
        boolean corsEnabled,

        List<URI> allowedCors
) {
}
