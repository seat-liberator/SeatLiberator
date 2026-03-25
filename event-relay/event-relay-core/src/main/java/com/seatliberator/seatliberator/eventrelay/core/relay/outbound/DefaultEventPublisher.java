package com.seatliberator.seatliberator.eventrelay.core.relay.outbound;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventPayloadSerializer;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceFactory;
import com.seatliberator.seatliberator.eventrelay.core.model.*;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import org.jspecify.annotations.NonNull;

import java.time.Clock;

public class DefaultEventPublisher implements EventPublisher {
    private final EventPayloadSerializer serializer;
    private final EventTraceFactory traceFactory;
    private final EventStore store;
    private final Clock clock;

    public DefaultEventPublisher(
            @NonNull EventPayloadSerializer serializer,
            @NonNull EventTraceFactory traceFactory,
            @NonNull EventStore store,
            @NonNull Clock clock
    ) {
        this.serializer = serializer;
        this.traceFactory = traceFactory;
        this.store = store;
        this.clock = clock;
    }

    @Override
    public <P extends EventPayload> void publish(
            @NonNull EventType type,
            @NonNull P payload
    ) {
        EventHeader header = ImmutableEventHeader.from(type);
        EventTrace trace = traceFactory.create();
        EventEnvelope envelope = ImmutableEventEnvelope.from(
                header,
                trace,
                serializer.stringify(payload)
        );

        store.accept(envelope, EventFlow.OUTBOUND, clock.instant());
    }
}
