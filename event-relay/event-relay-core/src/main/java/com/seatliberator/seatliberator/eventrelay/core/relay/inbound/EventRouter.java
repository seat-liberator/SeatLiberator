package com.seatliberator.seatliberator.eventrelay.core.relay.inbound;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface EventRouter {
    void route(@NonNull EventEnvelope envelope);
}
