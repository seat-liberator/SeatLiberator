package com.seatliberator.seatliberator.eventrelay.core.util;

import com.seatliberator.seatliberator.eventrelay.core.model.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class EventTestFixture {
    public static Clock createFixedClock(Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }

    public static Clock createFixedClock() {
        return Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    }

    public static EventHeader createHeader() {
        var type = new ImmutableEventType("reservation.create");
        return new ImmutableEventHeader(type);
    }

    public static EventTrace createTrace(
            String eventId,
            String causationId,
            String producer,
            String correlationId,
            Clock clock
    ) {
        var descriptor = new ImmutableEventAggregateDescriptor(getAggregateType(), getAggregateId());
        return new ImmutableEventTrace(
                eventId,
                causationId,
                producer,
                correlationId,
                descriptor,
                clock.instant()
        );
    }

    public static EventTrace createTrace(Clock clock) {
        return createTrace(
                "event-1",
                "parent-event-1",
                "reservation-service",
                "correlation-1",
                clock
        );
    }

    public static EventTrace createTrace() {
        return createTrace(createFixedClock());
    }

    public static EventEnvelope createEnvelope(Clock clock) {
        return ImmutableEventEnvelope.from(
                createHeader(),
                createTrace(clock),
                createRawPayload()
        );
    }

    public static EventEnvelope createEnvelope() {
        return createEnvelope(createFixedClock());
    }

    public static TestPayload createPayload() {
        return new TestPayload(
                getAggregateId(),
                getSeatFieldValue(),
                getUserFieldValue()
        );
    }

    public static String createRawPayload() {
        return new ObjectMapper().writeValueAsString(createPayload());
    }

    public static String getReservationFieldKey() {
        return "reservationId";
    }

    public static String getAggregateType() {
        return "reservation";
    }

    public static String getAggregateId() {
        return "reservation-1";
    }

    public static String getSeatFieldKey() {
        return "seatId";
    }

    public static String getSeatFieldValue() {
        return "seat-1";
    }

    public static String getUserFieldKey() {
        return "userId";
    }

    public static String getUserFieldValue() {
        return "user-1";
    }

    public record TestPayload(
            String reservationId,
            String seatId,
            String userId
    ) implements EventPayload {
    }
}
