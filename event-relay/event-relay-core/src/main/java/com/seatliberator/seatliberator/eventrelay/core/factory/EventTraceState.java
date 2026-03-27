package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.EventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface EventTraceState {
    @Nullable EventTrace sourceTrace();

    @Nullable EventAggregateDescriptor aggregateDescriptor();

    @Nullable String overrideCorrelationId();

    @Nullable String overrideCausationId();

    boolean disableInheritCorrelationId();

    boolean disableInheritCausationId();

    @NonNull EventTraceState clearOverrides();

    @NonNull EventTraceState copy();

    void restore(@NonNull EventTraceState state);

    @NonNull EventTraceState bindSourceTrace(@NonNull EventTrace sourceTrace);

    @NonNull EventTraceState withAggregate(
            @NonNull String aggregateType,
            @NonNull String aggregateId
    );

    @NonNull EventTraceState withCorrelationId(@NonNull String correlationId);

    @NonNull EventTraceState withoutCorrelationId();

    @NonNull EventTraceState withCausationId(@NonNull String causationId);

    @NonNull EventTraceState withoutCausationId();
}
