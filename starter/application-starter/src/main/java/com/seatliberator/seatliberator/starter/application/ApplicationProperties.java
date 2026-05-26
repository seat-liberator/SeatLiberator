package com.seatliberator.seatliberator.starter.application;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;

@Validated
@ConfigurationProperties(prefix = "seatliberator.starter.application")
public record ApplicationProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("UTC")
        @NotNull ZoneId timezone
) {
}
