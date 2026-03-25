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
            @NonNull Supplier<T> action,
            @NonNull EventTrace trace
    );

    void withSource(
            @NonNull Runnable action,
            @NonNull EventTrace trace
    );

    @NonNull EventTraceState current();

    void clear();
}
