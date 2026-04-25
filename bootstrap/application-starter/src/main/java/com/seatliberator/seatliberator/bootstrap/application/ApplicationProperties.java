package com.seatliberator.seatliberator.bootstrap.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.ZoneId;

@ConfigurationProperties(prefix = "seatliberator.application")
public record ApplicationProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("UTC")
        ZoneId zoneId
) {
    public ApplicationProperties {
        if (zoneId == null) throw new IllegalArgumentException("zoneId must not be null or blank.");
    }
}
