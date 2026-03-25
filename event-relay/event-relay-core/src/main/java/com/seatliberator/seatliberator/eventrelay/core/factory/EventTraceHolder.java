package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface EventTraceHolder {
    <T> @NonNull T with(
            @NonNull Supplier<T> action,
            @NonNull Consumer<EventTraceState> customizer
    );

    void with(
            @NonNull Runnable action,
            @NonNull Consumer<EventTraceState> customizer
    );

    <T> @NonNull T withSource(
            @NonNull EventTrace trace,
            @NonNull Supplier<T> action
    );

    void withSource(
            @NonNull EventTrace trace,
            @NonNull Runnable action
    );

    @NonNull EventTraceState current();

    void clear();
}
