package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import tools.jackson.databind.ObjectMapper;

public class JacksonEventPayloadSerializer implements EventPayloadSerializer {
    private final ObjectMapper objectMapper;

    public JacksonEventPayloadSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String stringify(EventPayload payload) {
        return objectMapper.writeValueAsString(payload);
    }
}
