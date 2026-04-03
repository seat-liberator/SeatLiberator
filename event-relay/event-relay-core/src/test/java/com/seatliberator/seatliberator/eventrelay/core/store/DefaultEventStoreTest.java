package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.exception.EventNotFoundException;
import com.seatliberator.seatliberator.eventrelay.core.store.exception.InvalidStateForTransitionException;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Default Event Store")
public class DefaultEventStoreTest {
    private DefaultEventStore createStore() {
        return new DefaultEventStore(new DefaultEventStateTransitionPolicy());
    }

    private EventEnvelope createEnvelope(String eventId, Instant createdAt) {
        Clock clock = EventTestFixture.createFixedClock(createdAt);
        return ImmutableEventEnvelope.from(
                EventTestFixture.createHeader(),
                EventTestFixture.createTrace(
                        eventId,
                        "parent-" + eventId,
                        "reservation-service",
                        "correlation-" + eventId,
                        clock
                ),
                EventTestFixture.createRawPayload()
        );
    }

    @Test
    @DisplayName("accept 는 이벤트를 저장한다")
    void acceptAndClaim() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:20Z");
        var envelope = createEnvelope("event-1", Instant.parse("2026-01-01T00:00:00Z"));

        store.accept(envelope, EventFlow.INBOUND, acceptedAt);
        var claimed = store.claimBatch(EventFlow.INBOUND, claimedAt, 10);

        assertEquals(1, claimed.size());
        assertEquals("event-1", claimed.getFirst().trace().eventId());
    }

    @Test
    @DisplayName("같은 eventId 를 두 번 accept 하면 예외가 발생한다")
    void accept_duplicateEventId_throws() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var envelope = createEnvelope("event-1", Instant.parse("2026-01-01T00:00:00Z"));

        store.accept(envelope, EventFlow.INBOUND, acceptedAt);

        assertThrows(
                IllegalStateException.class,
                () -> store.accept(envelope, EventFlow.INBOUND, acceptedAt)
        );
    }

    @Test
    @DisplayName("claimBatch 는 flow 가 일치하는 이벤트만 가져온다")
    void claimBatch_filtersByFlow() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:20Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:00Z")), EventFlow.INBOUND, acceptedAt);
        store.accept(createEnvelope("event-2", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.OUTBOUND, acceptedAt);

        var claimed = store.claimBatch(EventFlow.INBOUND, claimedAt, 10);

        assertAll(
                () -> assertEquals(1, claimed.size()),
                () -> assertEquals("event-1", claimed.getFirst().trace().eventId())
        );
    }

    @Test
    @DisplayName("claimBatch 는 createdAt 오래된 순으로 가져온다")
    void claimBatch_sortsByCreatedAt() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:20Z");

        store.accept(createEnvelope("event-2", Instant.parse("2026-01-01T00:00:02Z")), EventFlow.INBOUND, acceptedAt);
        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);
        store.accept(createEnvelope("event-3", Instant.parse("2026-01-01T00:00:03Z")), EventFlow.INBOUND, acceptedAt);

        var claimed = store.claimBatch(EventFlow.INBOUND, claimedAt, 10);

        assertEquals(
                List.of("event-1", "event-2", "event-3"),
                claimed.stream().map(e -> e.trace().eventId()).toList()
        );
    }

    @Test
    @DisplayName("claimBatch 는 batchSize 만큼만 가져온다")
    void claimBatch_limitsByBatchSize() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:20Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);
        store.accept(createEnvelope("event-2", Instant.parse("2026-01-01T00:00:02Z")), EventFlow.INBOUND, acceptedAt);
        store.accept(createEnvelope("event-3", Instant.parse("2026-01-01T00:00:03Z")), EventFlow.INBOUND, acceptedAt);

        var claimed = store.claimBatch(EventFlow.INBOUND, claimedAt, 2);

        assertEquals(
                List.of("event-1", "event-2"),
                claimed.stream().map(e -> e.trace().eventId()).toList()
        );
    }

    @Test
    @DisplayName("claimBatch 로 가져간 이벤트는 다시 claim 되지 않는다")
    void claimBatch_excludesAlreadyProcessing() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var firstClaimedAt = Instant.parse("2026-01-01T00:00:20Z");
        var secondClaimedAt = Instant.parse("2026-01-01T00:00:30Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);

        var first = store.claimBatch(EventFlow.INBOUND, firstClaimedAt, 10);
        var second = store.claimBatch(EventFlow.INBOUND, secondClaimedAt, 10);

        assertAll(
                () -> assertEquals(1, first.size()),
                () -> assertTrue(second.isEmpty())
        );
    }

    @Test
    @DisplayName("FAILED 된 이벤트는 다시 claim 할 수 있다")
    void claimBatch_includesFailedEvent() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var firstClaimedAt = Instant.parse("2026-01-01T00:00:20Z");
        var failedAt = Instant.parse("2026-01-01T00:00:30Z");
        var secondClaimedAt = Instant.parse("2026-01-01T00:00:40Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);

        var firstClaim = store.claimBatch(EventFlow.INBOUND, firstClaimedAt, 10);
        store.reportFailed(firstClaim.getFirst().trace().eventId(), failedAt);

        var secondClaim = store.claimBatch(EventFlow.INBOUND, secondClaimedAt, 10);

        assertEquals(1, secondClaim.size());
        assertEquals("event-1", secondClaim.getFirst().trace().eventId());
    }

    @Test
    @DisplayName("reportCompleted 는 PROCESSING 상태의 이벤트를 완료 처리한다")
    void reportCompleted() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:20Z");
        var resolvedAt = Instant.parse("2026-01-01T00:00:30Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);
        var claimed = store.claimBatch(EventFlow.INBOUND, claimedAt, 10);

        assertDoesNotThrow(() ->
                store.reportCompleted(claimed.getFirst().trace().eventId(), resolvedAt)
        );

        assertTrue(store.claimBatch(EventFlow.INBOUND, claimedAt.plusSeconds(10), 10).isEmpty());
    }

    @Test
    @DisplayName("reportFailed 는 PROCESSING 상태의 이벤트를 실패 처리한다")
    void reportFailed() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:20Z");
        var failedAt = Instant.parse("2026-01-01T00:00:30Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);
        var claimed = store.claimBatch(EventFlow.INBOUND, claimedAt, 10);

        assertDoesNotThrow(() ->
                store.reportFailed(claimed.getFirst().trace().eventId(), failedAt)
        );
    }

    @Test
    @DisplayName("존재하지 않는 eventId 를 완료 처리하면 예외가 발생한다")
    void reportCompleted_whenEventNotFound_throws() {
        var store = createStore();

        assertThrows(
                EventNotFoundException.class,
                () -> store.reportCompleted("missing-event", Instant.parse("2026-01-01T00:00:00Z"))
        );
    }

    @Test
    @DisplayName("claimBatch 의 claimedAt 이 acceptedAt 보다 빠르면 예외가 발생한다")
    void claimBatch_whenClaimedAtBeforeAcceptedAt_throws() {
        var store = createStore();
        var acceptedAt = Instant.parse("2026-01-01T00:00:10Z");
        var claimedAt = Instant.parse("2026-01-01T00:00:09Z");

        store.accept(createEnvelope("event-1", Instant.parse("2026-01-01T00:00:01Z")), EventFlow.INBOUND, acceptedAt);

        assertThrows(
                InvalidStateForTransitionException.class,
                () -> store.claimBatch(EventFlow.INBOUND, claimedAt, 10)
        );
    }
}
