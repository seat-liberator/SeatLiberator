package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import tools.jackson.databind.ObjectMapper;

public class JacksonEventEnvelopeSerializer implements EventEnvelopeSerializer {
    private final ObjectMapper objectMapper;

    public JacksonEventEnvelopeSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String stringify(EventEnvelope envelope) {
        return objectMapper.writeValueAsString(envelope);
    }
}
