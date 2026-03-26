package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

@Embeddable
public class JpaEventTrace implements EventTrace {
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "causation_id", nullable = true)
    private String causationId;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "aggregate_type")),
            @AttributeOverride(name = "id", column = @Column(name = "aggregate_id"))
    })
    private JpaEventAggregateDescriptor aggregateDescriptor;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public JpaEventTrace() {
    }

    public JpaEventTrace(
            @NonNull String eventId,
            @Nullable String causationId,
            @NonNull String producer,
            @NonNull String correlationId,
            @Nullable JpaEventAggregateDescriptor aggregateDescriptor,
            @NonNull Instant createdAt
    ) {
        this.eventId = eventId;
        this.causationId = causationId;
        this.producer = producer;
        this.correlationId = correlationId;
        this.aggregateDescriptor = aggregateDescriptor;
        this.createdAt = createdAt;
    }

    public static JpaEventTrace copyOf(@NonNull EventTrace trace) {
        var descriptor = Optional.ofNullable(trace.aggregateDescriptor())
                .map(JpaEventAggregateDescriptor::copyOf)
                .orElse(null);

        return new JpaEventTrace(
                trace.eventId(),
                trace.causationId(),
                trace.producer(),
                trace.correlationId(),
                descriptor,
                trace.createdAt()
        );
    }

    public static JpaEventTrace from(
            @NonNull String eventId,
            @Nullable String causationId,
            @NonNull String producer,
            @NonNull String correlationId,
            @Nullable EventAggregateDescriptor aggregateDescriptor,
            @NonNull Instant createdAt
    ) {
        var descriptor = Optional.ofNullable(aggregateDescriptor)
                .map(JpaEventAggregateDescriptor::copyOf)
                .orElse(null);

        return new JpaEventTrace(
                eventId,
                causationId,
                producer,
                correlationId,
                descriptor,
                createdAt
        );
    }

    @Override
    public @NonNull String eventId() {
        return eventId;
    }

    @Override
    public @Nullable String causationId() {
        return causationId;
    }

    @Override
    public @NonNull String producer() {
        return producer;
    }

    @Override
    public @NonNull String correlationId() {
        return correlationId;
    }

    @Override
    public @Nullable EventAggregateDescriptor aggregateDescriptor() {
        return aggregateDescriptor;
    }

    @Override
    public @NonNull Instant createdAt() {
        return createdAt;
    }
}
