package com.seatliberator.seatliberator.idempotency.core.store;

import com.seatliberator.seatliberator.idempotency.core.model.*;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean created = new AtomicBoolean(false);

        IdempotencyState state = states.compute(key, (ignored, exists) -> {
            if (exists == null) {
                created.set(true);
                return ImmutableIdempotencyState.create(key, initialContext, now);
            }
            return exists;
        });

        return new IdempotencyRecord(state, created.get());
    }

    @Override
    public boolean tryMarkRunning(@NonNull IdempotencyKey key) {
        Instant now = clock.instant();
        AtomicBoolean success = new AtomicBoolean(false);

        states.computeIfPresent(key, (ignored, exists) -> {
            DefaultExecutionState updatedExecutionState = DefaultExecutionState.of(exists.executionState());

            try {
                updatedExecutionState.markRunning(now);
            } catch (IllegalStateException e) {
                return exists;
            }

            ImmutableIdempotencyState updated = new ImmutableIdempotencyState(
                    ImmutableIdempotencyKey.of(exists.key()),
                    ImmutableIdempotencyContext.of(exists.context()),
                    ImmutableExecutionState.of(updatedExecutionState),
                    exists.createdAt()
            );

            success.set(true);
            return updated;
        });

        return success.get();
    }

    @Override
    public boolean tryMarkResolved(
            @NonNull IdempotencyKey key,
            @NonNull ExecutionOutput output
    ) {
        Instant now = clock.instant();
        AtomicBoolean success = new AtomicBoolean(false);

        states.computeIfPresent(key, (ignored, exists) -> {
            DefaultExecutionState updatedExecutionState = DefaultExecutionState.of(exists.executionState());

            try {
                updatedExecutionState.markResolved(now, output);
            } catch (IllegalStateException e) {
                return exists;
            }

            ImmutableIdempotencyState updated = new ImmutableIdempotencyState(
                    ImmutableIdempotencyKey.of(exists.key()),
                    ImmutableIdempotencyContext.of(exists.context()),
                    ImmutableExecutionState.of(updatedExecutionState),
                    exists.createdAt()
            );

            success.set(true);
            return updated;
        });

        return success.get();
    }

    @Override
    public boolean tryMarkExecutionError(@NonNull IdempotencyKey key) {
        AtomicBoolean success = new AtomicBoolean(false);

        states.computeIfPresent(key, (ignored, exists) -> {
            DefaultExecutionState updatedExecutionState = DefaultExecutionState.of(exists.executionState());

            try {
                updatedExecutionState.markExecutionError();
            } catch (IllegalStateException e) {
                return exists;
            }

            ImmutableIdempotencyState updated = new ImmutableIdempotencyState(
                    ImmutableIdempotencyKey.of(exists.key()),
                    ImmutableIdempotencyContext.of(exists.context()),
                    ImmutableExecutionState.of(updatedExecutionState),
                    exists.createdAt()
            );

            success.set(true);
            return updated;
        });

        return success.get();
    }
}
