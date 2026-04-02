package com.seatliberator.seatliberator.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "seatliberator.application")
public record WebApplicationProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("UTC")
        String zoneId
) {
}
