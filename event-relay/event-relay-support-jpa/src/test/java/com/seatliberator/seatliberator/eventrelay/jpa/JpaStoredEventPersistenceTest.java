package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventType;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaPostgresqlEventAcceptor;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEventPersistencePort;
import com.seatliberator.seatliberator.eventrelay.test.EventFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestApplication.class, TestConfig.class})
@Testcontainers
@DisplayName("Jpa Stored Event Persistence")
public class JpaStoredEventPersistenceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("event-relay-jpa-test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private JpaStoredEventPersistencePort persistence;

    @Autowired
    private JpaPostgresqlEventAcceptor acceptor;

    private final Clock clock = EventFixture.createFixedClock();

    @Test
    @DisplayName("claim은 flow와 status로 필터링하고 createdAt 오름차순으로 가져온다")
    void claim_filters_and_orders() {
        var envelope1 = EventFixture.createEnvelope(EventFixture.createFixedClock(Instant.parse("2026-03-26T00:00:05Z")));
        var envelope2 = EventFixture.createEnvelope(EventFixture.createFixedClock(Instant.parse("2026-03-26T00:00:01Z")));
        var envelope3 = EventFixture.createEnvelope(EventFixture.createFixedClock(Instant.parse("2026-03-26T00:00:04Z")));
        var envelope4 = EventFixture.createEnvelope(EventFixture.createFixedClock(Instant.parse("2026-03-26T00:00:03Z")));

        acceptor.accept(envelope1, EventFlow.OUTBOUND, Instant.parse("2026-03-26T00:00:02Z"));
        acceptor.accept(envelope2, EventFlow.INBOUND, Instant.parse("2026-03-26T00:00:01Z"));
        acceptor.accept(envelope3, EventFlow.OUTBOUND, Instant.parse("2026-03-26T00:00:03Z"));
        acceptor.accept(envelope4, EventFlow.OUTBOUND, Instant.parse("2026-03-26T00:00:05Z"));

        List<JpaStoredEvent> result = persistence.claim(
                List.of(EventStatus.PENDING, EventStatus.FAILED),
                EventFlow.OUTBOUND,
                2
        );

        assertThat(result).extracting(JpaStoredEvent::getId)
                .containsExactly(
                        envelope4.trace().eventId(),
                        envelope3.trace().eventId()
                );
    }

    @Test
    @DisplayName("markProcessing은 PENDING 상태를 PROCESSING으로 바꾼다")
    void markProcessing_from_pending() {
        Instant acceptedAt = clock.instant();
        Instant startedAt = acceptedAt.plusSeconds(5);

        var envelope = EventFixture.createEnvelope(clock);
        var eventId = envelope.trace().eventId();
        acceptor.accept(envelope, EventFlow.OUTBOUND, acceptedAt);

        int updated = persistence.markProcessing(eventId, startedAt);

        JpaStoredEvent reloaded = persistence.findAllByIds(List.of(eventId)).getFirst();
        assertThat(updated).isEqualTo(1);
        assertThat(reloaded.startedAt()).isEqualTo(startedAt);
        assertThat(reloaded.status()).isEqualTo(EventStatus.PROCESSING);
    }

    @Test
    @DisplayName("markProcessing은 FAILED 상태도 다시 PROCESSING으로 바꾼다")
    void markProcessing_from_failed() {
        Instant acceptedAt = clock.instant();
        Instant firstStartedAt = acceptedAt.plusSeconds(5);
        Instant failedAt = firstStartedAt.plusSeconds(5);
        Instant retriedAt = failedAt.plusSeconds(5);

        var envelope = EventFixture.createEnvelope(clock);
        var eventId = envelope.trace().eventId();
        acceptor.accept(envelope, EventFlow.OUTBOUND, acceptedAt);
        persistence.markProcessing(eventId, firstStartedAt);
        persistence.markResolved(eventId, EventStatus.FAILED, failedAt);

        int updated = persistence.markProcessing(eventId, retriedAt);

        JpaStoredEvent reloaded = persistence.findAllByIds(List.of(eventId)).getFirst();
        assertThat(updated).isEqualTo(1);
        assertThat(reloaded.status()).isEqualTo(EventStatus.PROCESSING);
        assertThat(reloaded.startedAt()).isEqualTo(retriedAt);
    }

    @Test
    @DisplayName("markProcessing은 이미 PROCESSING이면 아무것도 하지 않는다")
    void markProcessing_when_already_processing() {
        Instant acceptedAt = clock.instant();
        Instant startedAt = acceptedAt.plusSeconds(5);

        var envelope = EventFixture.createEnvelope(clock);
        var eventId = envelope.trace().eventId();
        acceptor.accept(envelope, EventFlow.OUTBOUND, acceptedAt);

        persistence.markProcessing(eventId, startedAt);

        int updated = persistence.markProcessing(eventId, startedAt.plusSeconds(1));

        JpaStoredEvent reloaded = persistence.findAllByIds(List.of(eventId)).getFirst();
        assertThat(updated).isZero();
        assertThat(reloaded.status()).isEqualTo(EventStatus.PROCESSING);
        assertThat(reloaded.startedAt()).isEqualTo(startedAt);
    }

    @Test
    @DisplayName("markResolved는 PROCESSING 상태를 COMPLETED로 바꾼다")
    void markResolved_to_completed() {
        Instant acceptedAt = clock.instant();
        Instant startedAt = acceptedAt.plusSeconds(5);
        Instant resolvedAt = startedAt.plusSeconds(5);

        var envelope = EventFixture.createEnvelope(clock);
        var eventId = envelope.trace().eventId();
        acceptor.accept(envelope, EventFlow.OUTBOUND, acceptedAt);

        persistence.markProcessing(eventId, startedAt);

        int updated = persistence.markResolved(eventId, EventStatus.COMPLETED, resolvedAt);

        JpaStoredEvent reloaded = persistence.findAllByIds(List.of(eventId)).getFirst();
        assertThat(updated).isEqualTo(1);
        assertThat(reloaded.status()).isEqualTo(EventStatus.COMPLETED);
        assertThat(reloaded.resolvedAt()).isEqualTo(resolvedAt);
    }

    @Test
    @DisplayName("markResolved는 PROCESSING 상태가 아니면 아무것도 하지 않는다")
    void markResolved_only_from_processing() {
        Instant acceptedAt = clock.instant();
        Instant startedAt = acceptedAt.plusSeconds(5);
        Instant resolvedAt = startedAt.plusSeconds(5);

        var envelope = EventFixture.createEnvelope(clock);
        var eventId = envelope.trace().eventId();

        acceptor.accept(envelope, EventFlow.OUTBOUND, acceptedAt);

        int updated = persistence.markResolved(eventId, EventStatus.COMPLETED, resolvedAt);

        JpaStoredEvent reloaded = persistence.findAllByIds(List.of(eventId)).getFirst();
        assertThat(updated).isZero();
        assertThat(reloaded.status()).isEqualTo(EventStatus.PENDING);
        assertThat(reloaded.resolvedAt()).isNull();
    }

    private JpaStoredEvent stored(
            String eventId,
            EventFlow flow,
            EventStatus status,
            Instant acceptedAt
    ) {
        return JpaStoredEvent.from(
                mockHeader("seat.reserved"),
                mockTrace(eventId, acceptedAt.minusSeconds(30)),
                "{\"seatId\":\"A-1\"}",
                flow,
                status,
                acceptedAt,
                null,
                null
        );
    }

    private EventHeader mockHeader(String typeName) {
        EventHeader header = mock(EventHeader.class);
        when(header.eventType()).thenReturn(ImmutableEventType.from(typeName));
        return header;
    }

    private EventTrace mockTrace(String eventId, Instant createdAt) {
        EventTrace trace = mock(EventTrace.class);
        when(trace.eventId()).thenReturn(eventId);
        when(trace.causationId()).thenReturn(null);
        when(trace.producer()).thenReturn("seat-service");
        when(trace.correlationId()).thenReturn("corr-" + eventId);
        when(trace.aggregateDescriptor()).thenReturn(null);
        when(trace.createdAt()).thenReturn(createdAt);
        return trace;
    }
}
