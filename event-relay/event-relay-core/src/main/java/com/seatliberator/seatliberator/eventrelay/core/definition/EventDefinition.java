package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;

public interface EventDefinition<P extends EventPayload> {
    @NonNull EventType type();

    @NonNull Class<P> payloadType();
}
