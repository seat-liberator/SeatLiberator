package com.seatliberator.seatliberator.eventrelay.autoconfigure.properties;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventRelayOptions;

public final class EventRelayOptionsMapper {
    public static EventRelayOptions toOptions(EventRelayProperties properties) {
        return new EventRelayOptions(properties.batchSize());
    }
}
