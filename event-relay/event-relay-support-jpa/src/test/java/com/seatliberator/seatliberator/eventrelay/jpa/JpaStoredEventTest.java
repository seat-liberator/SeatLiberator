package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.*;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Jpa Stored Event")
public class JpaStoredEventTest {

    @Test
    @DisplayName("create는 envelope으로부터 pending 상태의 stored event를 생성한다")
    void create_pending() {
        Instant createdAt = Instant.parse("2026-03-26T10:00:00Z");
        Instant acceptedAt = Instant.parse("2026-03-26T10:01:00Z");

        EventEnvelope envelope = mockEnvelope(
                "event-1",
                "producer-a",
                "corr-1",
                createdAt,
                "seat.reserved",
                "{\"seatId\":\"A-1\"}"
        );

        JpaStoredEvent stored = JpaStoredEvent.create(envelope, EventFlow.OUTBOUND, acceptedAt);

        assertThat(stored.getId()).isEqualTo("event-1");
        assertThat(stored.trace().eventId()).isEqualTo("event-1");
        assertThat(stored.header().eventType().name()).isEqualTo("seat.reserved");
        assertThat(stored.rawPayload()).isEqualTo("{\"seatId\":\"A-1\"}");
        assertThat(stored.flow()).isEqualTo(EventFlow.OUTBOUND);
        assertThat(stored.status()).isEqualTo(EventStatus.PENDING);
        assertThat(stored.acceptedAt()).isEqualTo(acceptedAt);
        assertThat(stored.startedAt()).isNull();
        assertThat(stored.resolvedAt()).isNull();
    }

    @Test
    @DisplayName("copyOf는 aggregateDescriptor를 포함한 값을 복사한다")
    void copy_with_aggregateDescriptor() {
        Instant createdAt = Instant.parse("2026-03-26T10:00:00Z");
        Instant acceptedAt = Instant.parse("2026-03-26T10:01:00Z");

        EventAggregateDescriptor descriptor = mock(EventAggregateDescriptor.class);
        when(descriptor.type()).thenReturn("seat");
        when(descriptor.id()).thenReturn("A-1");

        EventHeader header = mock(EventHeader.class);
        when(header.eventType()).thenReturn(ImmutableEventType.from("seat.reserved"));

        EventTrace trace = mock(EventTrace.class);
        when(trace.eventId()).thenReturn("event-2");
        when(trace.causationId()).thenReturn("cause-1");
        when(trace.producer()).thenReturn("producer-a");
        when(trace.correlationId()).thenReturn("corr-2");
        when(trace.aggregateDescriptor()).thenReturn(descriptor);
        when(trace.createdAt()).thenReturn(createdAt);

        JpaStoredEvent stored = JpaStoredEvent.from(
                header,
                trace,
                "{\"seatId\":\"A-1\"}",
                EventFlow.INBOUND,
                EventStatus.FAILED,
                acceptedAt,
                createdAt.plusSeconds(5),
                createdAt.plusSeconds(10)
        );

        JpaStoredEvent copied = JpaStoredEvent.copyOf(stored);

        assertThat(copied.getId()).isEqualTo("event-2");
        assertThat(copied.trace().eventId()).isEqualTo("event-2");
        assertThat(copied.trace().aggregateDescriptor()).isNotNull();
        assertThat(copied.trace().aggregateDescriptor().type()).isEqualTo("seat");
        assertThat(copied.trace().aggregateDescriptor().id()).isEqualTo("A-1");
        assertThat(copied.status()).isEqualTo(EventStatus.FAILED);
        assertThat(copied.startedAt()).isEqualTo(createdAt.plusSeconds(5));
        assertThat(copied.resolvedAt()).isEqualTo(createdAt.plusSeconds(10));
    }

    private EventEnvelope mockEnvelope(
            String eventId,
            String producer,
            String correlationId,
            Instant createdAt,
            String eventTypeName,
            String rawPayload
    ) {
        EventHeader header = mock(EventHeader.class);
        when(header.eventType()).thenReturn(ImmutableEventType.from(eventTypeName));

        EventTrace trace = mock(EventTrace.class);
        when(trace.eventId()).thenReturn(eventId);
        when(trace.causationId()).thenReturn(null);
        when(trace.producer()).thenReturn(producer);
        when(trace.correlationId()).thenReturn(correlationId);
        when(trace.aggregateDescriptor()).thenReturn(null);
        when(trace.createdAt()).thenReturn(createdAt);

        EventEnvelope envelope = mock(EventEnvelope.class);
        when(envelope.header()).thenReturn(header);
        when(envelope.trace()).thenReturn(trace);
        when(envelope.rawPayload()).thenReturn(rawPayload);
        return envelope;
    }
}

