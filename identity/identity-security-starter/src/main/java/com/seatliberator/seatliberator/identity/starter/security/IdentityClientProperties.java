package com.seatliberator.seatliberator.identity.starter.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

@ConfigurationProperties(prefix = "identity.client")
public record IdentityClientProperties(
        @DefaultValue("true")
        boolean enabled,

        URI jwkSetUri
) {
}
