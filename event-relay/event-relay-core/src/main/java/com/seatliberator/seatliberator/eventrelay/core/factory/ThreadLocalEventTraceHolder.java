package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadLocalEventTraceHolder implements EventTraceHolder {
    private final ThreadLocal<EventTraceState> local = ThreadLocal.withInitial(DefaultEventTraceState::new);

    @Override
    public @NonNull <T> T with(
            @NonNull Supplier<T> action,
            @NonNull Consumer<EventTraceState> customizer
    ) {
        var state = local.get();
        var copied = state.copy();

        customizer.accept(state);

        try {
            return action.get();
        } finally {
            state.restore(copied);
        }
    }

    @Override
    public void with(
            @NonNull Runnable action,
            @NonNull Consumer<EventTraceState> customizer
    ) {
        with(() -> {
            action.run();
            return null;
        }, customizer);
    }

    @Override
    public @NonNull <T> T withSource(
            @NonNull EventTrace trace,
            @NonNull Supplier<T> action
    ) {
        var state = local.get();
        var copied = state.copy();

        state
                .bindSourceTrace(trace)
                .clearOverrides();

        try {
            return action.get();
        } finally {
            state.restore(copied);
        }
    }

    @Override
    public void withSource(
            @NonNull EventTrace trace,
            @NonNull Runnable action
    ) {
        withSource(
                trace,
                () -> {
                    action.run();
                    return null;
                }
        );
    }

    @Override
    public @NonNull EventTraceState current() {
        return local.get();
    }

    @Override
    public void clear() {
        local.remove();
    }
}
