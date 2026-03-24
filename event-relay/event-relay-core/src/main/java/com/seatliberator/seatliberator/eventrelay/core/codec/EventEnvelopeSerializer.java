package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;

public interface EventEnvelopeSerializer {
    String stringify(EventEnvelope envelope);
}
