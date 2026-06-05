package com.seatliberator.seatliberator.starter.launcher;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "seatliberator.starter.launcher")
public record SpringApplicationLauncherProperties(
        @DefaultValue("true")
        boolean enabled,

        @Valid
        @DefaultValue
        @NotNull
        Seed seed
) {
    public record Seed(
            @DefaultValue("false")
            boolean enabled
    ) {
    }
}
