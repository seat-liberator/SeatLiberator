package com.seatliberator.seatliberator.eventrelay.core.relay.outbound;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;

public interface EventPublisher {
    <P extends EventPayload> void publish(
            @NonNull EventType type,
            @NonNull P payload
    );
}
