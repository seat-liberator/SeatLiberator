package com.seatliberator.seatliberator.eventrelay.core.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "event-relay.store")
public record EventStoreConfigurationProperties(
        @DefaultValue("100") int batchSize
) {
}