package com.seatliberator.seatliberator.eventrelay.core.relay.inbound;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventPayloadDeserializer;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import org.jspecify.annotations.NonNull;

public class RegistryEventRouter implements EventRouter {
    private final EventListenerRegistry listenerRegistry;
    private final EventPayloadDeserializer payloadDeserializer;
    private final EventTraceHolder traceHolder;

    public RegistryEventRouter(
            @NonNull EventListenerRegistry listenerRegistry,
            @NonNull EventPayloadDeserializer payloadDeserializer,
            @NonNull EventTraceHolder traceHolder
    ) {
        this.listenerRegistry = listenerRegistry;
        this.payloadDeserializer = payloadDeserializer;
        this.traceHolder = traceHolder;
    }

    @Override
    public void route(@NonNull EventEnvelope envelope) {
        var type = envelope.header().eventType();
        var listener = listenerRegistry.resolve(type);

        if (listener == null) {
            throw new IllegalStateException("Missing listener");
        }

        traceHolder.withSource(
                envelope.trace(),
                () -> handle(listener, envelope)
        );
    }

    private <P extends EventPayload> void handle(EventListener<P> listener, EventEnvelope envelope) {
        P payload = payloadDeserializer.materialize(envelope.rawPayload(), listener.definition().payloadType());
        listener.handle(envelope, payload);
    }
}
