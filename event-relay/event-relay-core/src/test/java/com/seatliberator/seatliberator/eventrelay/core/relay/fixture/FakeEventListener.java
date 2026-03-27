package com.seatliberator.seatliberator.eventrelay.core.relay.fixture;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListener;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.jspecify.annotations.NonNull;

public class FakeEventListener implements EventListener<EventTestFixture.TestPayload> {
    private final EventDefinition<EventTestFixture.TestPayload> definition;
    private EventEnvelope envelope;
    private EventTestFixture.TestPayload payload;

    public FakeEventListener(EventDefinition<EventTestFixture.TestPayload> definition) {
        this.definition = definition;
    }

    @Override
    public @NonNull EventDefinition<EventTestFixture.TestPayload> definition() {
        return definition;
    }

    @Override
    public void handle(@NonNull EventEnvelope envelope, EventTestFixture.@NonNull TestPayload payload) {
        this.envelope = envelope;
        this.payload = payload;
    }

    public EventEnvelope lastHandledEnvelope() {
        return this.envelope;
    }

    public EventPayload lastHandledPayload() {
        return this.payload;
    }

    public EventTrace traceInHandler() {
        return this.envelope.trace();
    }
}
