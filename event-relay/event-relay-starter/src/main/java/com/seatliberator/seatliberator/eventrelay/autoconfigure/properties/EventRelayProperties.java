package com.seatliberator.seatliberator.eventrelay.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "event-relay")
public record EventRelayProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("100")
        int batchSize
) {
}
