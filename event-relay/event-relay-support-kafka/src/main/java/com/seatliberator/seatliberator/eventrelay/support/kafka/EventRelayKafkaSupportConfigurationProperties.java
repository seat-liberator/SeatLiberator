package com.seatliberator.seatliberator.eventrelay.support.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "event-relay.kafka")
public record EventRelayKafkaSupportConfigurationProperties(
        @DefaultValue("event") String topicSuffix,
        @DefaultValue(".") String topicDelimiter,
        @DefaultValue("1") long sendTimeout
) {
}
