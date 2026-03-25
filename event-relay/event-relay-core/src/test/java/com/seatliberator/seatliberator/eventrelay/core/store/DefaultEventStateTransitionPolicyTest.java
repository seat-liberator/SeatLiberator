package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.exception.InvalidStateForTransitionException;
import com.seatliberator.seatliberator.eventrelay.core.store.model.DefaultStoredEvent;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Default Event State Transition Policy")
public class DefaultEventStateTransitionPolicyTest {

    private final DefaultEventStateTransitionPolicy policy = new DefaultEventStateTransitionPolicy();

    @Test
    @DisplayName("PENDING 상태는 PROCESSING 으로 전이할 수 있다")
    void validateMarkProcessing_fromPending() {
        var acceptedAt = Instant.parse("2026-01-01T00:00:00Z");
        var startedAt = Instant.parse("2026-01-01T00:00:01Z");
        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                acceptedAt
        );

        assertDoesNotThrow(() -> policy.validateMarkProcessing(state, startedAt));
    }

    @Test
    @DisplayName("FAILED 상태는 PROCESSING 으로 재전이할 수 있다")
    void validateMarkProcessing_fromFailed() {
        var acceptedAt = Instant.parse("2026-01-01T00:00:00Z");
        var failedAt = Instant.parse("2026-01-01T00:00:01Z");
        var retriedAt = Instant.parse("2026-01-01T00:00:02Z");

        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                acceptedAt
        );
        state.markProcessing(acceptedAt.plusSeconds(1));
        state.markFailed(failedAt);

        assertDoesNotThrow(() -> policy.validateMarkProcessing(state, retriedAt));
    }

    @Test
    @DisplayName("PROCESSING 상태에서는 다시 PROCESSING 으로 전이할 수 없다")
    void validateMarkProcessing_fromProcessing_throws() {
        var acceptedAt = Instant.parse("2026-01-01T00:00:00Z");
        var startedAt = Instant.parse("2026-01-01T00:00:01Z");

        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                acceptedAt
        );
        state.markProcessing(startedAt);

        assertThrows(
                InvalidStateForTransitionException.class,
                () -> policy.validateMarkProcessing(state, startedAt.plusSeconds(1))
        );
    }

    @Test
    @DisplayName("startAt 이 acceptedAt 보다 빠르면 PROCESSING 전이는 실패한다")
    void validateMarkProcessing_whenStartAtBeforeAcceptedAt_throws() {
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var startedAt = Instant.parse("2026-01-01T00:00:09Z");

        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                acceptedAt
        );

        assertThrows(
                InvalidStateForTransitionException.class,
                () -> policy.validateMarkProcessing(state, startedAt)
        );
    }

    @Test
    @DisplayName("PROCESSING 상태는 COMPLETED 로 전이할 수 있다")
    void validateMarkCompleted_success() {
        var acceptedAt = Instant.parse("2026-01-01T00:00:00Z");
        var startedAt = Instant.parse("2026-01-01T00:00:01Z");
        var resolvedAt = Instant.parse("2026-01-01T00:00:02Z");

        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                acceptedAt
        );
        state.markProcessing(startedAt);

        assertDoesNotThrow(() -> policy.validateMarkCompleted(state, resolvedAt));
    }

    @Test
    @DisplayName("PROCESSING 이 아닌 상태에서는 COMPLETED 로 전이할 수 없다")
    void validateMarkCompleted_whenNotProcessing_throws() {
        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );

        assertThrows(
                InvalidStateForTransitionException.class,
                () -> policy.validateMarkCompleted(state, Instant.parse("2026-01-01T00:00:01Z"))
        );
    }

    @Test
    @DisplayName("resolvedAt 이 startedAt 보다 빠르면 COMPLETED 전이는 실패한다")
    void validateMarkCompleted_whenResolvedAtBeforeStartedAt_throws() {
        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        state.markProcessing(Instant.parse("2026-01-01T00:00:10Z"));

        assertThrows(
                InvalidStateForTransitionException.class,
                () -> policy.validateMarkCompleted(state, Instant.parse("2026-01-01T00:00:09Z"))
        );
    }

    @Test
    @DisplayName("PROCESSING 상태는 FAILED 로 전이할 수 있다")
    void validateMarkFailed_success() {
        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        state.markProcessing(Instant.parse("2026-01-01T00:00:01Z"));

        assertDoesNotThrow(() ->
                policy.validateMarkFailed(state, Instant.parse("2026-01-01T00:00:02Z"))
        );
    }

    @Test
    @DisplayName("resolvedAt 이 startedAt 보다 빠르면 FAILED 전이는 실패한다")
    void validateMarkFailed_whenResolvedAtBeforeStartedAt_throws() {
        var state = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        state.markProcessing(Instant.parse("2026-01-01T00:00:10Z"));

        assertThrows(
                InvalidStateForTransitionException.class,
                () -> policy.validateMarkFailed(state, Instant.parse("2026-01-01T00:00:09Z"))
        );
    }
}
