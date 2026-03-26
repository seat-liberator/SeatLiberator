package com.seatliberator.seatliberator.eventrelay.core.store;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event-relay.store")
public record EventStoreConfigurationProperties(
        int batchSize
) {
}