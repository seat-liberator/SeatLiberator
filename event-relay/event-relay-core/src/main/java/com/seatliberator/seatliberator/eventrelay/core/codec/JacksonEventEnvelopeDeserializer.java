package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.codec.exception.EventEnvelopeSerializationException;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventEnvelope;
import org.jspecify.annotations.NonNull;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class JacksonEventEnvelopeDeserializer implements EventEnvelopeDeserializer {
    private final ObjectMapper objectMapper;

    public JacksonEventEnvelopeDeserializer(@NonNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @NonNull EventEnvelope materialize(@NonNull String raw) {
        try {
            return objectMapper.readValue(raw, ImmutableEventEnvelope.class);
        } catch (JacksonException e) {
            throw new EventEnvelopeSerializationException(e);
        }
    }
}
