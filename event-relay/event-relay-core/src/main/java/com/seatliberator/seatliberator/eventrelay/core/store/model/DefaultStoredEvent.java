package com.seatliberator.seatliberator.eventrelay.core.store.model;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public class DefaultStoredEvent implements StoredEvent {
    private final EventHeader header;
    private final EventTrace trace;
    private final String rawPayload;
    private final EventFlow flow;
    private final Instant acceptedAt;
    private EventStatus status;
    private Instant startedAt;
    private Instant resolvedAt;

    public DefaultStoredEvent(
            @NonNull EventHeader header,
            @NonNull EventTrace trace,
            @NonNull String rawPayload,
            @NonNull EventFlow flow,
            @NonNull EventStatus status,
            @NonNull Instant acceptedAt,
            @Nullable Instant startedAt,
            @Nullable Instant resolvedAt
    ) {
        this.header = header;
        this.trace = trace;
        this.rawPayload = rawPayload;
        this.flow = flow;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
    }

    public static DefaultStoredEvent of(@NonNull StoredEvent event) {
        return new DefaultStoredEvent(
                event.header(),
                event.trace(),
                event.rawPayload(),
                event.flow(),
                event.status(),
                event.acceptedAt(),
                event.startedAt(),
                event.resolvedAt()
        );
    }

    public static DefaultStoredEvent create(
            @NonNull EventEnvelope envelope,
            @NonNull EventFlow flow,
            @NonNull Instant acceptedAt
    ) {
        return new DefaultStoredEvent(
                envelope.header(),
                envelope.trace(),
                envelope.rawPayload(),
                flow,
                EventStatus.PENDING,
                acceptedAt,
                null,
                null
        );
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
