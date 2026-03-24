package com.seatliberator.seatliberator.eventrelay.core.model.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import org.jspecify.annotations.NonNull;

public interface EventEnvelopeFactory {
    @NonNull EventEnvelope copy(@NonNull EventEnvelope envelope);

    @NonNull EventEnvelope create(
            @NonNull EventHeader header,
            @NonNull EventTrace trace,
            @NonNull String rawPayload
    );
}
