package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;

public interface EventPayloadSerializer {
    String stringify(EventPayload payload);
}
