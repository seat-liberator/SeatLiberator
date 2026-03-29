package com.seatliberator.seatliberator.eventrelay.test;

import com.seatliberator.seatliberator.eventrelay.core.model.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

public class EventFixture {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final AtomicInteger counter = new AtomicInteger();

    private static final String TEST_AGGREGATE_TYPE = "reservation";
    private static final String TEST_PRODUCER = "reservation-application";

    public static Clock createFixedClock(Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }

    public static Clock createFixedClock() {
        return Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    }

    public static EventEnvelope createEnvelope(Clock clock) {
        var count = counter.incrementAndGet();
        var now = clock.instant();
        return ImmutableEventEnvelope.from(
                createHeader(),
                createTrace(count, now),
                createRawPayload(count)
        );
    }

    private static EventHeader createHeader() {
        var type = ImmutableEventType.from("reservation.create");
        return new ImmutableEventHeader(type);
    }

    private static EventTrace createTrace(
            String eventId,
            String causationId,
            String producer,
            String correlationId,
            EventAggregateDescriptor descriptor,
            Instant createdAt
    ) {
        return ImmutableEventTrace.from(
                eventId,
                causationId,
                producer,
                correlationId,
                descriptor,
                createdAt
        );
    }

    private static EventTrace createTrace(int count, Instant now) {
        var descriptor = ImmutableEventAggregateDescriptor.from(TEST_AGGREGATE_TYPE, TEST_AGGREGATE_TYPE + "-id-" + count);

        return createTrace(
                "event-id-" + count,
                "causation-id-" + count,
                TEST_PRODUCER,
                "correlation-id-" + count,
                descriptor,
                now
        );
    }

    private static TestEventPayload createPayload(int count) {
        return new TestEventPayload("title-" + count, "description-" + count);
    }

    private static String createRawPayload(int count) {
        return objectMapper.writeValueAsString(createPayload(count));
    }

    public record TestEventPayload(String title, String description) implements EventPayload {
    }
}
