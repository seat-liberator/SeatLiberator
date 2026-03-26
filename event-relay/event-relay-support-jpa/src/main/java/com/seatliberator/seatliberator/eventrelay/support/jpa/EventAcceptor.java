package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

public interface EventAcceptor {
    void accept(
            @NonNull EventEnvelope envelope,
            @NonNull EventFlow flow,
            @NonNull Instant acceptedAt
    );
}
