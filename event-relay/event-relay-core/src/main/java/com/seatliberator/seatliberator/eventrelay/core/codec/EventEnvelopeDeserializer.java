package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import org.jspecify.annotations.NonNull;

public interface EventEnvelopeDeserializer {
    @NonNull EventEnvelope materialize(@NonNull String raw);
}
