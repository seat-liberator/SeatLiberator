package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventTrace;
import com.seatliberator.seatliberator.eventrelay.core.provider.CorrelationIdProvider;
import com.seatliberator.seatliberator.eventrelay.core.provider.EventIdProvider;
import com.seatliberator.seatliberator.eventrelay.core.provider.ProducerProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Clock;
import java.util.Optional;

public class ImmutableEventTraceFactory implements EventTraceFactory {
    private final EventTraceHolder eventTraceHolder;
    private final EventIdProvider eventIdProvider;
    private final ProducerProvider producerProvider;
    private final CorrelationIdProvider correlationIdProvider;
    private final Clock clock;

    public ImmutableEventTraceFactory(
            @NonNull EventTraceHolder eventTraceHolder,
            @NonNull EventIdProvider eventIdProvider,
            @NonNull ProducerProvider producerProvider,
            @NonNull CorrelationIdProvider correlationIdProvider,
            @NonNull Clock clock
    ) {
        this.eventTraceHolder = eventTraceHolder;
        this.eventIdProvider = eventIdProvider;
        this.producerProvider = producerProvider;
        this.correlationIdProvider = correlationIdProvider;
        this.clock = clock;
    }

    @Override
    public @NonNull EventTrace create() {
        var state = eventTraceHolder.current();

        var aggregateDescriptor = Optional.ofNullable(state.aggregateDescriptor())
                .orElseThrow(() -> new IllegalArgumentException("Missing aggregate descriptor"));

        return ImmutableEventTrace.from(
                eventIdProvider.get(),
                resolveCausationId(state),
                producerProvider.get(),
                resolveCorrelationId(state),
                aggregateDescriptor,
                clock.instant()
        );
    }

    private @NonNull String resolveCorrelationId(@NonNull EventTraceState state) {
        var override = state.overrideCorrelationId();
        if (override != null) return override;
        if (state.disableInheritCorrelationId()) return correlationIdProvider.get();
        return Optional.ofNullable(state.sourceTrace())
                .map(EventTrace::correlationId)
                .orElseGet(correlationIdProvider::get);
    }

    private @Nullable String resolveCausationId(@NonNull EventTraceState state) {
        var override = state.overrideCausationId();
        if (override != null) return override;
        if (state.disableInheritCausationId()) return null;
        return Optional.ofNullable(state.sourceTrace())
                .map(EventTrace::eventId)
                .orElse(null);
    }
}
