package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.codec.exception.EventPayloadDeserializationException;
import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class JacksonEventPayloadDeserializer implements EventPayloadDeserializer {
    private final ObjectMapper objectMapper;

    public JacksonEventPayloadDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <P extends EventPayload> P materialize(String rawPayload, Class<P> expect) {
        try {
            return objectMapper.readValue(rawPayload, expect);
        } catch (JacksonException e) {
            throw new EventPayloadDeserializationException(e);
        }
    }
}
