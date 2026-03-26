package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import com.seatliberator.seatliberator.eventrelay.core.store.model.StoredEvent;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "stored_event")
public class JpaStoredEvent implements StoredEvent {

    @Id
    private String id;

    @Embedded
    private JpaEventHeader header;

    @Embedded
    private JpaEventTrace trace;

    @Column(name = "raw_payload", nullable = false)
    private String rawPayload;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow", nullable = false)
    private EventFlow flow;

    @Column(name = "accepted_at", nullable = false)
    private Instant acceptedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "started_at", nullable = true)
    private Instant startedAt;

    @Column(name = "resolved_at", nullable = true)
    private Instant resolvedAt;

    public JpaStoredEvent() {
    }

    public JpaStoredEvent(
            @NonNull String id,
            @NonNull JpaEventHeader header,
            @NonNull JpaEventTrace trace,
            @NonNull String rawPayload,
            @NonNull EventFlow flow,
            @NonNull EventStatus status,
            @NonNull Instant acceptedAt,
            @Nullable Instant startedAt,
            @Nullable Instant resolvedAt
    ) {
        this.id = id;
        this.header = header;
        this.trace = trace;
        this.rawPayload = rawPayload;
        this.flow = flow;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
    }

    public static JpaStoredEvent copyOf(@NonNull StoredEvent storedEvent) {
        return new JpaStoredEvent(
                storedEvent.trace().eventId(),
                JpaEventHeader.copyOf(storedEvent.header()),
                JpaEventTrace.copyOf(storedEvent.trace()),
                storedEvent.rawPayload(),
                storedEvent.flow(),
                storedEvent.status(),
                storedEvent.acceptedAt(),
                storedEvent.startedAt(),
                storedEvent.resolvedAt()
        );
    }

    public static JpaStoredEvent from(
            @NonNull EventHeader header,
            @NonNull EventTrace trace,
            @NonNull String rawPayload,
            @NonNull EventFlow flow,
            @NonNull EventStatus status,
            @NonNull Instant acceptedAt,
            @Nullable Instant startedAt,
            @Nullable Instant resolvedAt
    ) {
        return new JpaStoredEvent(
                trace.eventId(),
                JpaEventHeader.copyOf(header),
                JpaEventTrace.copyOf(trace),
                rawPayload,
                flow,
                status,
                acceptedAt,
                startedAt,
                resolvedAt
        );
    }

    public static JpaStoredEvent create(
            @NonNull EventEnvelope envelope,
            @NonNull EventFlow flow,
            @NonNull Instant acceptedAt
    ) {
        return new JpaStoredEvent(
                envelope.trace().eventId(),
                JpaEventHeader.copyOf(envelope.header()),
                JpaEventTrace.copyOf(envelope.trace()),
                envelope.rawPayload(),
                flow,
                EventStatus.PENDING,
                acceptedAt,
                null,
                null
        );
    }

    public String getId() {
        return id;
    }

    @Override
    public @NonNull EventHeader header() {
        return header;
    }

    @Override
    public @NonNull EventTrace trace() {
        return trace;
    }

    @Override
    public @NonNull String rawPayload() {
        return rawPayload;
    }

    @Override
    public @NonNull EventFlow flow() {
        return flow;
    }

    @Override
    public @NonNull EventStatus status() {
        return status;
    }

    @Override
    public @NonNull Instant acceptedAt() {
        return acceptedAt;
    }

    @Override
    public @Nullable Instant startedAt() {
        return startedAt;
    }

    @Override
    public @Nullable Instant resolvedAt() {
        return resolvedAt;
    }

    @Override
    public void markProcessing(@NonNull Instant startedAt) {
        this.status = EventStatus.PROCESSING;
        this.startedAt = startedAt;
    }

    @Override
    public void markCompleted(@NonNull Instant resolvedAt) {
        this.status = EventStatus.COMPLETED;
        this.resolvedAt = resolvedAt;
    }

    @Override
    public void markFailed(@NonNull Instant resolvedAt) {
        this.status = EventStatus.FAILED;
        this.resolvedAt = resolvedAt;
    }
}
