package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.core.store.exception.EventNotFoundException;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JpaEventStore implements EventStore {
    private final JpaStoredEventPersistencePort persistence;
    private final EventAcceptor acceptor;
    private final int batchSize;

    public JpaEventStore(
            @NonNull JpaStoredEventPersistencePort persistence,
            @NonNull EventAcceptor acceptor,
            int batchSize
    ) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize는 1보다 작을 수 없습니다");
        }
        this.persistence = persistence;
        this.acceptor = acceptor;
        this.batchSize = batchSize;
    }

    @Transactional
    @Override
    public void accept(@NonNull EventEnvelope envelope, @NonNull EventFlow flow, @NonNull Instant acceptedAt) {
        acceptor.accept(envelope, flow, acceptedAt);
    }

    @Transactional
    @Override
    public List<EventEnvelope> claimBatch(@NonNull EventFlow flow, @NonNull Instant claimedAt) {
        var candidates = persistence.claim(List.of(EventStatus.PENDING, EventStatus.FAILED), flow, batchSize);
        var claimedIds = new ArrayList<String>();

        for (var candidate : candidates) {
            int updated = persistence.markProcessing(candidate.getId(), claimedAt);
            if (updated == 1) {
                claimedIds.add(candidate.getId());
            }
        }

        if (claimedIds.isEmpty()) return List.of();

        return persistence.findAllByIds(claimedIds).stream()
                .<EventEnvelope>map(ImmutableEventEnvelope::copyOf)
                .sorted(Comparator.comparing(e -> e.trace().createdAt()))
                .toList();
    }

    @Transactional
    @Override
    public void reportCompleted(@NonNull String eventId, @NonNull Instant resolvedAt) {
        int updated = persistence.markResolved(eventId, EventStatus.COMPLETED, resolvedAt);
        if (updated == 0) {
            throw new EventNotFoundException(eventId);
        }
    }

    @Transactional
    @Override
    public void reportFailed(@NonNull String eventId, @NonNull Instant resolvedAt) {
        int updated = persistence.markResolved(eventId, EventStatus.FAILED, resolvedAt);
        if (updated == 0) {
            throw new EventNotFoundException(eventId);
        }
    }
}
