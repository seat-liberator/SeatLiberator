package com.seatliberator.seatliberator.eventrelay.core.relay;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventPayloadSerializer;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceFactory;
import com.seatliberator.seatliberator.eventrelay.core.model.*;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.DefaultEventPublisher;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;


@DisplayName("Default Event Publisher")
public class DefaultEventPublisherTest {


    @Test
    @DisplayName("이벤트를 직렬화하여 OUTBOUND flow로 저장한다")
    void publish() {
        var serializer = mock(EventPayloadSerializer.class);
        var traceFactory = mock(EventTraceFactory.class);
        var store = mock(EventStore.class);

        var fixedInstant = Instant.parse("2026-03-26T00:00:00Z");
        var clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        var payload = new TestPayload("hello");
        var type = new TestEventType("seat.reserved");

        var trace = ImmutableEventTrace.from(
                "evt-1",
                null,
                "seat-service",
                "corr-1",
                ImmutableEventAggregateDescriptor.from("seat", "1"),
                Instant.parse("2026-03-25T23:59:59Z")
        );

        given(serializer.stringify(payload)).willReturn("{\"message\":\"hello\"}");
        given(traceFactory.create()).willReturn(trace);

        var publisher = new DefaultEventPublisher(
                serializer,
                traceFactory,
                store,
                clock
        );

        publisher.publish(type, payload);

        var envelopeCaptor = ArgumentCaptor.forClass(EventEnvelope.class);

        then(store).should().accept(
                envelopeCaptor.capture(),
                eq(EventFlow.OUTBOUND),
                eq(fixedInstant)
        );

        var envelope = envelopeCaptor.getValue();

        assertThat(envelope.header().eventType().name()).isEqualTo(type.name());
        assertThat(envelope.trace()).isEqualTo(trace);
        assertThat(envelope.rawPayload()).isEqualTo("{\"message\":\"hello\"}");
    }

    @Test
    @DisplayName("이벤트 저장 시 헤더를 이벤트 타입으로 생성한다")
    void header() {
        var serializer = mock(EventPayloadSerializer.class);
        var traceFactory = mock(EventTraceFactory.class);
        var store = mock(EventStore.class);

        var fixedInstant = Instant.parse("2026-03-26T00:00:00Z");
        var clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        var payload = new TestPayload("hello");
        var type = new TestEventType("seat.reserved");

        var trace = ImmutableEventTrace.from(
                "evt-1",
                null,
                "seat-service",
                "corr-1",
                ImmutableEventAggregateDescriptor.from("seat", "1"),
                Instant.parse("2026-03-25T23:59:59Z")
        );

        given(serializer.stringify(payload)).willReturn("{\"message\":\"hello\"}");
        given(traceFactory.create()).willReturn(trace);

        var publisher = new DefaultEventPublisher(
                serializer,
                traceFactory,
                store,
                clock
        );

        publisher.publish(type, payload);

        var envelopeCaptor = ArgumentCaptor.forClass(EventEnvelope.class);

        then(store).should().accept(
                envelopeCaptor.capture(),
                eq(EventFlow.OUTBOUND),
                eq(fixedInstant)
        );

        var envelope = envelopeCaptor.getValue();

        assertThat(envelope.header().eventType().name()).isEqualTo("seat.reserved");
    }

    private record TestPayload(String message) implements EventPayload {
    }

    private record TestEventType(String name) implements EventType {
    }
}
