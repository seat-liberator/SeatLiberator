package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import tools.jackson.databind.ObjectMapper;

public class JacksonEventPayloadDeserializer implements EventPayloadDeserializer {
    private final ObjectMapper objectMapper;

    public JacksonEventPayloadDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <P extends EventPayload> P materialize(String rawPayload, Class<P> expect) {
        return objectMapper.readValue(rawPayload, expect);
    }
}
