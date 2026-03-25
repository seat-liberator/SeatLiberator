package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.EventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventTrace;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class DefaultEventTraceState implements EventTraceState {
    private EventTrace sourceTrace;
    private EventAggregateDescriptor aggregateDescriptor;
    private String overrideCorrelationId;
    private String overrideCausationId;
    private boolean disableInheritCorrelationId = false;
    private boolean disableInheritCausationId = false;

    public DefaultEventTraceState() {
    }

    public DefaultEventTraceState(
            @Nullable EventTrace sourceTrace,
            @Nullable EventAggregateDescriptor aggregateDescriptor,
            @Nullable String overrideCorrelationId,
            @Nullable String overrideCausationId,
            boolean disableInheritCorrelationId,
            boolean disableInheritCausationId
    ) {
        this.sourceTrace = sourceTrace;
        this.aggregateDescriptor = aggregateDescriptor;
        this.overrideCorrelationId = overrideCorrelationId;
        this.overrideCausationId = overrideCausationId;
        this.disableInheritCorrelationId = disableInheritCorrelationId;
        this.disableInheritCausationId = disableInheritCausationId;
    }

    public static DefaultEventTraceState copyOf(@NonNull EventTraceState state) {
        var sourceTrace = Optional.ofNullable(state.sourceTrace())
                .map(ImmutableEventTrace::copyOf)
                .orElse(null);
        var aggregateDescriptor = Optional.ofNullable(state.aggregateDescriptor())
                .map(ImmutableEventAggregateDescriptor::copyOf)
                .orElse(null);
        return new DefaultEventTraceState(
                sourceTrace,
                aggregateDescriptor,
                state.overrideCorrelationId(),
                state.overrideCausationId(),
                state.disableInheritCorrelationId(),
                state.disableInheritCausationId()
        );
    }

    @Override
    public @NonNull EventTrace sourceTrace() {
        return sourceTrace;
    }

    @Override
    public @NonNull EventAggregateDescriptor aggregateDescriptor() {
        return aggregateDescriptor;
    }

    @Override
    public @Nullable String overrideCorrelationId() {
        return overrideCorrelationId;
    }

    @Override
    public @Nullable String overrideCausationId() {
        return overrideCausationId;
    }

    @Override
    public boolean disableInheritCorrelationId() {
        return disableInheritCorrelationId;
    }

    @Override
    public boolean disableInheritCausationId() {
        return disableInheritCausationId;
    }

    @Override
    public @NonNull EventTraceState clearOverrides() {
        this.aggregateDescriptor = null;
        this.overrideCorrelationId = null;
        this.overrideCausationId = null;
        this.disableInheritCorrelationId = false;
        this.disableInheritCausationId = false;

        return this;
    }

    @Override
    public @NonNull EventTraceState copy() {
        return copyOf(this);
    }

    @Override
    public void restore(@NonNull EventTraceState state) {
        this.sourceTrace = Optional.ofNullable(state.sourceTrace())
                .map(ImmutableEventTrace::copyOf)
                .orElse(null);
        this.aggregateDescriptor = Optional.ofNullable(state.aggregateDescriptor())
                .map(ImmutableEventAggregateDescriptor::copyOf)
                .orElse(null);
        this.overrideCorrelationId = state.overrideCorrelationId();
        this.overrideCausationId = state.overrideCausationId();
        this.disableInheritCorrelationId = state.disableInheritCorrelationId();
        this.disableInheritCausationId = state.disableInheritCausationId();
    }

    @Override
    public @NonNull EventTraceState bindSourceTrace(@NonNull EventTrace sourceTrace) {
        this.sourceTrace = ImmutableEventTrace.copyOf(sourceTrace);
        return this;
    }

    @Override
    public @NonNull EventTraceState withAggregate(@NonNull String aggregateType, @NonNull String aggregateId) {
        this.aggregateDescriptor = ImmutableEventAggregateDescriptor.from(aggregateType, aggregateId);
        return this;
    }

    @Override
    public @NonNull EventTraceState withCorrelationId(@NonNull String correlationId) {
        this.disableInheritCorrelationId = true;
        this.overrideCorrelationId = correlationId;
        return this;
    }

    @Override
    public @NonNull EventTraceState withoutCorrelationId() {
        this.overrideCorrelationId = null;
        this.disableInheritCorrelationId = true;
        return this;
    }

    @Override
    public @NonNull EventTraceState withCausationId(@NonNull String causationId) {
        this.overrideCausationId = causationId;
        this.disableInheritCausationId = true;
        return this;
    }

    @Override
    public @NonNull EventTraceState withoutCausationId() {
        this.overrideCausationId = null;
        this.disableInheritCausationId = true;
        return this;
    }
}
