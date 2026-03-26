package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface JpaStoredEventRepository extends JpaRepository<JpaStoredEvent, String> {

    @Query("""
            SELECT e
            FROM JpaStoredEvent e
            WHERE e.flow = :flow
                AND e.status IN :statuses
            ORDER BY e.acceptedAt ASC""")
    List<JpaStoredEvent> claim(
            @NonNull @Param("statuses") List<EventStatus> statuses,
            @NonNull @Param("flow") EventFlow flow,
            @NonNull Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE JpaStoredEvent e
            SET e.status = 'PROCESSING', e.startedAt = :startedAt
            WHERE e.id = :id
                AND (e.status = 'PENDING' OR e.status = 'FAILED')""")
    int markProcessing(
            @NonNull @Param("id") String id,
            @NonNull @Param("startedAt") Instant startedAt
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE JpaStoredEvent e
            SET e.status = :status, e.resolvedAt = :resolvedAt
            WHERE e.id = :id
                AND e.status = 'PROCESSING'""")
    int markResolved(
            @NonNull @Param("id") String id,
            @NonNull @Param("status") EventStatus status,
            @NonNull @Param("resolvedAt") Instant resolvedAt
    );
}
