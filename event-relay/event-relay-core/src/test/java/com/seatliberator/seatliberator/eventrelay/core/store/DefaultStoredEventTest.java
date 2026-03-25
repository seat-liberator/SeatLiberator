package com.seatliberator.seatliberator.eventrelay.core.store;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.DefaultStoredEvent;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Default Stored Event")
public class DefaultStoredEventTest {

    @Test
    @DisplayName("create 는 PENDING 상태의 StoredEvent 를 생성한다")
    void create() {
        var envelope = EventTestFixture.createEnvelope();
        var acceptedAt = Instant.parse("2026-01-01T00:00:00Z");

        var event = DefaultStoredEvent.create(envelope, EventFlow.INBOUND, acceptedAt);

        assertAll(
                () -> assertEquals(envelope.header(), event.header()),
                () -> assertEquals(envelope.trace(), event.trace()),
                () -> assertEquals(envelope.rawPayload(), event.rawPayload()),
                () -> assertEquals(EventFlow.INBOUND, event.flow()),
                () -> assertEquals(EventStatus.PENDING, event.status()),
                () -> assertEquals(acceptedAt, event.acceptedAt()),
                () -> assertNull(event.startedAt()),
                () -> assertNull(event.resolvedAt())
        );
    }

    @Test
    @DisplayName("markProcessing 은 상태와 startedAt 을 변경한다")
    void markProcessing() {
        var event = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.OUTBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        var startedAt = Instant.parse("2026-01-01T00:00:05Z");

        event.markProcessing(startedAt);

        assertAll(
                () -> assertEquals(EventStatus.PROCESSING, event.status()),
                () -> assertEquals(startedAt, event.startedAt()),
                () -> assertNull(event.resolvedAt())
        );
    }

    @Test
    @DisplayName("markCompleted 는 상태와 resolvedAt 을 변경한다")
    void markCompleted() {
        var event = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.OUTBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        var resolvedAt = Instant.parse("2026-01-01T00:00:10Z");

        event.markCompleted(resolvedAt);

        assertAll(
                () -> assertEquals(EventStatus.COMPLETED, event.status()),
                () -> assertEquals(resolvedAt, event.resolvedAt())
        );
    }

    @Test
    @DisplayName("markFailed 는 상태와 resolvedAt 을 변경한다")
    void markFailed() {
        var event = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.OUTBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        var resolvedAt = Instant.parse("2026-01-01T00:00:10Z");

        event.markFailed(resolvedAt);

        assertAll(
                () -> assertEquals(EventStatus.FAILED, event.status()),
                () -> assertEquals(resolvedAt, event.resolvedAt())
        );
    }

    @Test
    @DisplayName("of 는 기존 StoredEvent 의 값을 복사한다")
    void of() {
        var original = DefaultStoredEvent.create(
                EventTestFixture.createEnvelope(),
                EventFlow.INBOUND,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        original.markProcessing(Instant.parse("2026-01-01T00:00:01Z"));
        original.markFailed(Instant.parse("2026-01-01T00:00:02Z"));

        var copied = DefaultStoredEvent.of(original);

        assertAll(
                () -> assertNotSame(original, copied),
                () -> assertEquals(original.header(), copied.header()),
                () -> assertEquals(original.trace(), copied.trace()),
                () -> assertEquals(original.rawPayload(), copied.rawPayload()),
                () -> assertEquals(original.flow(), copied.flow()),
                () -> assertEquals(original.status(), copied.status()),
                () -> assertEquals(original.acceptedAt(), copied.acceptedAt()),
                () -> assertEquals(original.startedAt(), copied.startedAt()),
                () -> assertEquals(original.resolvedAt(), copied.resolvedAt())
        );
    }
}
