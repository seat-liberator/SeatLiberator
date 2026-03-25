package com.seatliberator.seatliberator.eventrelay.core.relay.inbound;

import org.jspecify.annotations.NonNull;

public interface EventListenerRegistrar {
    void register(@NonNull EventListener<?> listener);
}
