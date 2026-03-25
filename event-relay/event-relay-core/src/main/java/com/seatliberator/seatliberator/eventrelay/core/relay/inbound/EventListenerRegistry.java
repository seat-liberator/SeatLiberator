package com.seatliberator.seatliberator.eventrelay.core.relay.inbound;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface EventListenerRegistry {
    @Nullable EventListener<?> resolve(EventType type);

    @NonNull List<EventListener<?>> listAll();
}
