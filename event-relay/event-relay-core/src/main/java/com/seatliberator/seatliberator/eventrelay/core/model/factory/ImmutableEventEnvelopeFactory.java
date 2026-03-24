package com.seatliberator.seatliberator.eventrelay.core.model.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.*;
import org.jspecify.annotations.NonNull;

public class ImmutableEventEnvelopeFactory implements EventEnvelopeFactory {
    @Override
    public @NonNull EventEnvelope copy(@NonNull EventEnvelope envelope) {
        return ImmutableEventEnvelope.copyOf(envelope);
    }

    @Override
    public @NonNull EventEnvelope create(@NonNull EventHeader header, @NonNull EventTrace trace, @NonNull String rawPayload) {
        var h = ImmutableEventHeader.copyOf(header);
        var t = ImmutableEventTrace.copyOf(trace);
        return new ImmutableEventEnvelope(h, t, rawPayload);
    }
}
