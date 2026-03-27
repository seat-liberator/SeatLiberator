package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeDeserializer;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jspecify.annotations.NonNull;

import java.time.Clock;

public class DefaultEndpointHandler implements EndpointHandler {
    private final EventStore store;
    private final EventEnvelopeDeserializer deserializer;
    private final Clock clock;

    public DefaultEndpointHandler(
            @NonNull EventStore store,
            @NonNull EventEnvelopeDeserializer deserializer,
            @NonNull Clock clock
    ) {
        this.store = store;
        this.deserializer = deserializer;
        this.clock = clock;
    }

    public void onMessage(@NonNull ConsumerRecord<String, String> record) {
        EventEnvelope envelope = deserializer.materialize(record.value());
        store.accept(envelope, EventFlow.INBOUND, clock.instant());
    }
}
