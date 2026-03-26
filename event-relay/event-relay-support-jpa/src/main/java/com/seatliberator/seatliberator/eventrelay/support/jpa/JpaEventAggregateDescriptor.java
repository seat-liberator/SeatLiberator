package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventAggregateDescriptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.jspecify.annotations.NonNull;

@Embeddable
public class JpaEventAggregateDescriptor implements EventAggregateDescriptor {
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "id", nullable = false)
    private String id;

    public JpaEventAggregateDescriptor() {
    }

    public JpaEventAggregateDescriptor(
            @NonNull String type,
            @NonNull String id
    ) {
        this.type = type;
        this.id = id;
    }

    public static @NonNull JpaEventAggregateDescriptor copyOf(@NonNull EventAggregateDescriptor aggregateDescriptor) {
        return new JpaEventAggregateDescriptor(
                aggregateDescriptor.type(),
                aggregateDescriptor.id()
        );
    }

    public static @NonNull JpaEventAggregateDescriptor from(
            @NonNull String type,
            @NonNull String id
    ) {
        return new JpaEventAggregateDescriptor(type, id);
    }

    @Override
    public @NonNull String type() {
        return type;
    }

    @Override
    public @NonNull String id() {
        return id;
    }
}
