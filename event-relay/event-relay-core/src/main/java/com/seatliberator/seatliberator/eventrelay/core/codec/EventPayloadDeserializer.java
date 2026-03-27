package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;

public interface EventPayloadDeserializer {
    <P extends EventPayload> P materialize(String rawPayload, Class<P> expect);
}
