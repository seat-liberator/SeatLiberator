package com.seatliberator.seatliberator.eventrelay.core.relay.inbound;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import org.jspecify.annotations.NonNull;

public interface EventListener<P extends EventPayload> {
    @NonNull EventDefinition<P> definition();

    void handle(
            @NonNull EventEnvelope envelope,
            @NonNull P payload
    );
}
