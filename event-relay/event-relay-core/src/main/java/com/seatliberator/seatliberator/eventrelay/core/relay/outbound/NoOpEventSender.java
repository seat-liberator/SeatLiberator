package com.seatliberator.seatliberator.eventrelay.core.relay.outbound;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpEventSender implements EventSender {
    private static final Logger log = LoggerFactory.getLogger(NoOpEventSender.class);

    @Override
    public void send(@NonNull EventEnvelope envelope) throws Exception {
        var eventId = envelope.trace().eventId();
        var eventType = envelope.header().eventType().name();
        var rawPayload = envelope.rawPayload();

        log.debug("message send. type={}, id={}, raw={}", eventId, eventType, rawPayload);
    }
}
