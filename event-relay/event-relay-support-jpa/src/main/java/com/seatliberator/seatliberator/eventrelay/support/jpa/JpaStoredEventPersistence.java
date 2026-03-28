package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Transactional
public class JpaStoredEventPersistence implements JpaStoredEventPersistencePort {
    @PersistenceContext
    private final EntityManager em;

    public JpaStoredEventPersistence(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<JpaStoredEvent> claim(List<EventStatus> statuses, EventFlow flow, int limit) {
        return em.createQuery("""
                        SELECT e
                        FROM JpaStoredEvent e
                        WHERE e.flow = :flow
                            AND e.status IN :statuses
                        ORDER BY e.trace.createdAt ASC""", JpaStoredEvent.class)
                .setParameter("flow", flow)
                .setParameter("statuses", statuses)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public int markProcessing(String id, Instant startedAt) {
        return em.createQuery("""
                        UPDATE JpaStoredEvent e
                        SET e.status = :processing, e.startedAt = :startedAt
                        WHERE e.id = :id
                            AND (e.status = :pending OR e.status = :failed)""")
                .setParameter("processing", EventStatus.PROCESSING)
                .setParameter("id", id)
                .setParameter("startedAt", startedAt)
                .setParameter("pending", EventStatus.PENDING)
                .setParameter("failed", EventStatus.FAILED)
                .executeUpdate();
    }

    @Override
    public int markResolved(String id, EventStatus status, Instant resolvedAt) {
        return em.createQuery("""
                UPDATE JpaStoredEvent e
                SET e.status = :status, e.resolvedAt = :resolvedAt
                WHERE e.id = :id
                    AND e.status = :processing""")
                .setParameter("id", id)
                .setParameter("status", status)
                .setParameter("resolvedAt", resolvedAt)
                .setParameter("processing", EventStatus.PROCESSING)
                .executeUpdate();
    }

    @Override
    public List<JpaStoredEvent> findAllByIds(List<String> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        return em.createQuery("""
                SELECT e
                FROM JpaStoredEvent e
                WHERE e.id IN :ids""", JpaStoredEvent.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
