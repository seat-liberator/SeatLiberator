package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.store.model.EventState;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

public interface EventStateTransitionPolicy {
    void validateMarkProcessing(
            @NonNull EventState state,
            @NonNull Instant startAt
    );

    void validateMarkCompleted(
            @NonNull EventState state,
            @NonNull Instant resolvedAt
    );

    void validateMarkFailed(
            @NonNull EventState state,
            @NonNull Instant resolvedAt
    );
}
