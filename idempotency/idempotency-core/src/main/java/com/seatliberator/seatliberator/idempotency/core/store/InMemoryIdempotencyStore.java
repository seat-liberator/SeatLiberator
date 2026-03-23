package com.seatliberator.seatliberator.idempotency.core.store;

import com.seatliberator.seatliberator.idempotency.core.model.*;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class InMemoryIdempotencyStore implements IdempotencyStore {
    private final ConcurrentHashMap<IdempotencyKey, IdempotencyState> states = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryIdempotencyStore(Clock clock) {
        this.clock = clock;
    }

    @Override
    public @NonNull IdempotencyRecord getOrCreate(
            @NonNull IdempotencyKey key,
            @NonNull IdempotencyContext initialContext
    ) {
        Instant now = clock.instant();
        var normKey = normKey(key);
        AtomicBoolean created = new AtomicBoolean(false);

        IdempotencyState state = states.compute(normKey, (ignored, exists) -> {
            if (exists == null) {
                created.set(true);
                return ImmutableIdempotencyState.create(normKey, initialContext, now);
            }
            return exists;
        });

        return new IdempotencyRecord(state, created.get());
    }

    @Override
    public boolean tryMarkRunning(@NonNull IdempotencyKey key) {
        Instant now = clock.instant();
        return tryTransition(normKey(key), state -> {
            var copy = DefaultExecutionState.of(state);
            copy.markRunning(now);
            return copy;
        });
    }

    @Override
    public boolean tryMarkResolved(
            @NonNull IdempotencyKey key,
            @NonNull ExecutionOutput output
    ) {
        Instant now = clock.instant();
        return tryTransition(normKey(key), state -> {
            var copy = DefaultExecutionState.of(state);
            copy.markResolved(now, output);
            return copy;
        });
    }

    @Override
    public boolean tryMarkExecutionError(@NonNull IdempotencyKey key) {
        return tryTransition(normKey(key), state -> {
            var copy = DefaultExecutionState.of(state);
            copy.markExecutionError();
            return copy;
        });
    }

    private ImmutableIdempotencyKey normKey(IdempotencyKey key) {
        return ImmutableIdempotencyKey.of(key);
    }

    private boolean tryTransition(
            @NonNull IdempotencyKey key,
            @NonNull Function<ExecutionState, ExecutionState> transition
    ) {

        AtomicBoolean success = new AtomicBoolean(false);

        states.computeIfPresent(key, (ignored, exists) -> {
            final ExecutionState state;

            try {
                state = transition.apply(exists.executionState());
            } catch (IllegalStateException | IllegalArgumentException e) {
                return exists;
            }

            success.set(true);

            return new ImmutableIdempotencyState(
                    ImmutableIdempotencyKey.of(exists.key()),
                    ImmutableIdempotencyContext.of(exists.context()),
                    ImmutableExecutionState.of(state),
                    exists.createdAt()
            );
        });

        return success.get();
    }
}
