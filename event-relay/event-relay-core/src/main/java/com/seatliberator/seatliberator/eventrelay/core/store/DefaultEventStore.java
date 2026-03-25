package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.exception.EventNotFoundException;
import com.seatliberator.seatliberator.eventrelay.core.store.model.DefaultStoredEvent;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventState;
import com.seatliberator.seatliberator.eventrelay.core.store.model.StoredEvent;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEventStore implements EventStore {
    private final Map<String, StoredEvent> repository = new HashMap<>();
    private final EventStateTransitionPolicy transitionPolicy;
    private final int batchSize;
    private final Object lock = new Object();

    public DefaultEventStore(
            @NonNull EventStateTransitionPolicy transitionPolicy,
            int batchSize
    ) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize는 1보다 작을 수 없습니다.");
        }
        this.transitionPolicy = transitionPolicy;
        this.batchSize = batchSize;
    }

    @Override
    public void accept(
            @NonNull EventEnvelope envelope,
            @NonNull EventFlow flow,
            @NonNull Instant acceptedAt
    ) {
        synchronized (lock) {
            var event = DefaultStoredEvent.create(envelope, flow, acceptedAt);
            var eventId = envelope.trace().eventId();

            var previous = repository.putIfAbsent(eventId, event);
            if (previous != null) {
                throw new IllegalStateException("Duplicated event id: " + eventId);
            }
        }
    }

    @Override
    public List<EventEnvelope> claimBatch(
            @NonNull EventFlow flow,
            @NonNull Instant claimedAt
    ) {
        synchronized (lock) {
            var candidate = repository.values().stream()
                    .filter(EventState::isPending)
                    .filter(e -> e.flow() == flow)
                    .sorted(Comparator.comparing(e -> e.trace().createdAt()))
                    .limit(batchSize)
                    .toList();

            candidate.forEach(e -> transitionPolicy.validateMarkProcessing(e, claimedAt));
            candidate.forEach(e -> e.markProcessing(claimedAt));

            return candidate.stream()
                    .<EventEnvelope>map(ImmutableEventEnvelope::copyOf)
                    .toList();
        }
    }

    @Override
    public void reportCompleted(
            @NonNull String eventId,
            @NonNull Instant resolvedAt
    ) {
        synchronized (lock) {
            var event = find(eventId);
            transitionPolicy.validateMarkCompleted(event, resolvedAt);
            event.markCompleted(resolvedAt);
        }
    }

    @Override
    public void reportFailed(
            @NonNull String eventId,
            @NonNull Instant resolvedAt
    ) {
        synchronized (lock) {
            var event = find(eventId);
            transitionPolicy.validateMarkFailed(event, resolvedAt);
            event.markFailed(resolvedAt);
        }
    }

    private StoredEvent find(String eventId) {
        var event = repository.get(eventId);
        if (event == null) {
            throw new EventNotFoundException(eventId);
        }
        return event;
    }
}
