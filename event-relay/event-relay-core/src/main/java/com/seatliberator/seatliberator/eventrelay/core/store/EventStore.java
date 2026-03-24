package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.List;

public interface EventStore {
    void accept(
            @NonNull EventEnvelope envelope,
            @NonNull EventFlow flow,
            @NonNull Instant acceptedAt
    );

    List<EventEnvelope> claimBatch(
            @NonNull EventFlow flow,
            @NonNull Instant claimedAt
    );

    void reportCompleted(
            @NonNull String eventId,
            @NonNull Instant resolvedAt
    );

    void reportFailed(
            @NonNull String eventId,
            @NonNull Instant resolvedAt
    );
}
