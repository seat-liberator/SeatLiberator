package com.seatliberator.seatliberator.eventrelay.core.relay.fixture;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceState;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FakeEventTraceHolder implements EventTraceHolder {

    private EventTrace sourceTrace;

    @Override
    public <T> T with(@NonNull Supplier<T> action, @NonNull Consumer<EventTraceState> customizer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void with(@NonNull Runnable action, @NonNull Consumer<EventTraceState> customizer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> @NonNull T withSource(@NonNull Supplier<T> action, @NonNull EventTrace trace) {
        var previous = sourceTrace;
        sourceTrace = trace;
        try {
            return action.get();
        } finally {
            sourceTrace = previous;
        }
    }

    @Override
    public void withSource(@NonNull Runnable action, @NonNull EventTrace trace) {
        var previous = sourceTrace;
        sourceTrace = trace;
        try {
            action.run();
        } finally {
            sourceTrace = previous;
        }
    }

    @Override
    public @NonNull EventTraceState current() {
        return new EventTraceState() {
            @Override
            public EventTrace sourceTrace() {
                return sourceTrace;
            }

            @Override
            public com.seatliberator.seatliberator.eventrelay.core.model.EventAggregateDescriptor aggregateDescriptor() {
                return null;
            }

            @Override
            public String overrideCorrelationId() {
                return null;
            }

            @Override
            public String overrideCausationId() {
                return null;
            }

            @Override
            public boolean disableInheritCorrelationId() {
                return false;
            }

            @Override
            public boolean disableInheritCausationId() {
                return false;
            }

            @Override
            public @NonNull EventTraceState clearOverrides() {
                return this;
            }

            @Override
            public @NonNull EventTraceState copy() {
                return this;
            }

            @Override
            public void restore(@NonNull EventTraceState state) {
            }

            @Override
            public @NonNull EventTraceState bindSourceTrace(@NonNull EventTrace sourceTrace) {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NonNull EventTraceState withAggregate(@NonNull String aggregateType, @NonNull String aggregateId) {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NonNull EventTraceState withCorrelationId(@NonNull String correlationId) {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NonNull EventTraceState withoutCorrelationId() {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NonNull EventTraceState withCausationId(@NonNull String causationId) {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NonNull EventTraceState withoutCausationId() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void clear() {
        sourceTrace = null;
    }
}