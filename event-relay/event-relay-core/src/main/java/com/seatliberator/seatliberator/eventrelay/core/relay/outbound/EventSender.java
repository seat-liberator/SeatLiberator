package com.seatliberator.seatliberator.eventrelay.core.relay.outbound;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface EventSender {
    void send(@NonNull EventEnvelope envelope);
}
