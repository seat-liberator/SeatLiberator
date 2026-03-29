package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public class JpaPostgresqlEventAcceptor implements EventAcceptor {
    private final EntityManager em;

    public JpaPostgresqlEventAcceptor(@NonNull EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public void accept(@NonNull EventEnvelope envelope, @NonNull EventFlow flow, @NonNull Instant acceptedAt) {
        var e = JpaStoredEvent.create(envelope, flow, acceptedAt);

        var aggregate = e.trace().aggregateDescriptor();
        var aggregateType = aggregate != null ? aggregate.type() : null;
        var aggregateId = aggregate != null ? aggregate.id() : null;

        var query = em.createNativeQuery("""
                INSERT INTO stored_event (
                    id,
                    event_type,
                    event_id,
                    causation_id,
                    producer,
                    correlation_id,
                    aggregate_type,
                    aggregate_id,
                    created_at,
                    raw_payload,
                    flow,
                    accepted_at,
                    status,
                    started_at,
                    resolved_at
                )
                VALUES (
                    :id,
                    :eventType,
                    :eventId,
                    :causationId,
                    :producer,
                    :correlationId,
                    :aggregateType,
                    :aggregateId,
                    :createdAt,
                    :rawPayload,
                    :flow,
                    :acceptedAt,
                    :status,
                    :startedAt,
                    :resolvedAt
                )
                ON CONFLICT DO NOTHING
                """);

        query.setParameter("id", e.getId());
        query.setParameter("eventType", e.header().eventType().name());
        query.setParameter("eventId", e.trace().eventId());
        query.setParameter("causationId", e.trace().causationId());
        query.setParameter("producer", e.trace().producer());
        query.setParameter("correlationId", e.trace().correlationId());
        query.setParameter("aggregateType", aggregateType);
        query.setParameter("aggregateId", aggregateId);
        query.setParameter("createdAt", e.trace().createdAt());
        query.setParameter("rawPayload", e.rawPayload());
        query.setParameter("flow", e.flow().name());
        query.setParameter("acceptedAt", e.acceptedAt());
        query.setParameter("status", e.status().name());
        query.setParameter("startedAt", e.startedAt());
        query.setParameter("resolvedAt", e.resolvedAt());

        int inserted = query.executeUpdate();

        if (inserted == 0) {
            throw new IllegalStateException("Duplicated event id: " + e.getId());
        }
    }
}
