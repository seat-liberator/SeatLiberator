package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.store.exception.InvalidStateForTransitionException;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventState;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

public class DefaultEventStateTransitionPolicy implements EventStateTransitionPolicy {
    @Override
    public void validateMarkProcessing(@NonNull EventState state, @NonNull Instant startAt) {
        if (!(state.status() == EventStatus.PENDING || state.status() == EventStatus.FAILED)) {
            throw new InvalidStateForTransitionException(state, EventStatus.PROCESSING);
        }
        if (startAt.isBefore(state.acceptedAt())) {
            throw new InvalidStateForTransitionException(
                    state,
                    EventStatus.PROCESSING,
                    "startAt",
                    startAt,
                    "acceptedAt",
                    state.acceptedAt()
            );
        }
    }

    @Override
    public void validateMarkCompleted(@NonNull EventState state, @NonNull Instant resolvedAt) {
        if (state.status() != EventStatus.PROCESSING) {
            throw new InvalidStateForTransitionException(state, EventStatus.COMPLETED);
        }
        Instant startedAt = requireStartedAt(state, EventStatus.COMPLETED);
        validateResolvedAtNotBeforeStartedAt(state, EventStatus.COMPLETED, resolvedAt, startedAt);
    }

    @Override
    public void validateMarkFailed(@NonNull EventState state, @NonNull Instant resolvedAt) {
        if (state.status() != EventStatus.PROCESSING) {
            throw new InvalidStateForTransitionException(state, EventStatus.FAILED);
        }
        Instant startedAt = requireStartedAt(state, EventStatus.FAILED);
        validateResolvedAtNotBeforeStartedAt(state, EventStatus.FAILED, resolvedAt, startedAt);
    }

    private Instant requireStartedAt(EventState state, EventStatus target) {
        Instant startedAt = state.startedAt();
        if (startedAt == null) {
            throw new InvalidStateForTransitionException(state, target, "startedAt은 null일 수 없습니다.");
        }
        return startedAt;
    }

    private void validateResolvedAtNotBeforeStartedAt(
            EventState state,
            EventStatus target,
            Instant resolvedAt,
            Instant startedAt
    ) {
        if (resolvedAt.isBefore(startedAt)) {
            throw new InvalidStateForTransitionException(
                    state,
                    target,
                    "resolvedAt",
                    resolvedAt,
                    "startedAt",
                    startedAt
            );
        }
    }
}
