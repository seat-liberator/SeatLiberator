package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventType;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestApplication.class)
@Testcontainers
@DisplayName("Jpa Stored Event Repository")
public class JpaStoredEventRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("event-relay-jpa-test")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private JpaStoredEventRepository repository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    @DisplayName("claim은 flow와 status로 필터링하고 acceptedAt 오름차순으로 가져온다")
    void claim_filters_and_orders() {
        repository.saveAndFlush(stored("e-3", EventFlow.OUTBOUND, EventStatus.FAILED, Instant.parse("2026-03-26T10:03:00Z")));
        repository.saveAndFlush(stored("e-1", EventFlow.OUTBOUND, EventStatus.PENDING, Instant.parse("2026-03-26T10:01:00Z")));
        repository.saveAndFlush(stored("e-2", EventFlow.OUTBOUND, EventStatus.PENDING, Instant.parse("2026-03-26T10:02:00Z")));
        repository.saveAndFlush(stored("e-4", EventFlow.INBOUND, EventStatus.PENDING, Instant.parse("2026-03-26T10:00:00Z")));
        repository.saveAndFlush(stored("e-5", EventFlow.OUTBOUND, EventStatus.PROCESSING, Instant.parse("2026-03-26T10:00:30Z")));

        List<JpaStoredEvent> result = repository.claim(
                List.of(EventStatus.PENDING, EventStatus.FAILED),
                EventFlow.OUTBOUND,
                PageRequest.of(0, 2)
        );

        assertThat(result).extracting(JpaStoredEvent::getId)
                .containsExactly("e-1", "e-2");
    }

    @Test
    @DisplayName("markProcessing은 PENDING 상태를 PROCESSING으로 바꾼다")
    void markProcessing_from_pending() {
        Instant acceptedAt = Instant.parse("2026-03-26T10:00:00Z");
        Instant startedAt = Instant.parse("2026-03-26T10:05:00Z");

        repository.saveAndFlush(stored("e-1", EventFlow.OUTBOUND, EventStatus.PENDING, acceptedAt));

        int updated = repository.markProcessing("e-1", startedAt);

        JpaStoredEvent reloaded = repository.findById("e-1").orElseThrow();
        assertThat(updated).isEqualTo(1);
        assertThat(reloaded.startedAt()).isEqualTo(startedAt);
        assertThat(reloaded.status()).isEqualTo(EventStatus.PROCESSING);
    }

    @Test
    @DisplayName("markProcessing은 FAILED 상태도 다시 PROCESSING으로 바꾼다")
    void markProcessing_from_failed() {
        Instant acceptedAt = Instant.parse("2026-03-26T10:00:00Z");
        Instant startedAt = Instant.parse("2026-03-26T10:05:00Z");

        repository.saveAndFlush(stored("e-2", EventFlow.OUTBOUND, EventStatus.FAILED, acceptedAt));

        int updated = repository.markProcessing("e-2", startedAt);

        JpaStoredEvent reloaded = repository.findById("e-2").orElseThrow();
        assertThat(updated).isEqualTo(1);
        assertThat(reloaded.status()).isEqualTo(EventStatus.PROCESSING);
        assertThat(reloaded.startedAt()).isEqualTo(startedAt);
    }

    @Test
    @DisplayName("markProcessing은 이미 PROCESSING이면 아무것도 하지 않는다")
    void markProcessing_when_already_processing() {
        Instant acceptedAt = Instant.parse("2026-03-26T10:00:00Z");

        repository.saveAndFlush(
                JpaStoredEvent.from(
                        mockHeader("seat.reserved"),
                        mockTrace("e-3", Instant.parse("2026-03-26T09:59:00Z")),
                        "{\"seatId\":\"A-1\"}",
                        EventFlow.OUTBOUND,
                        EventStatus.PROCESSING,
                        acceptedAt,
                        Instant.parse("2026-03-26T10:01:00Z"),
                        null
                )
        );

        int updated = repository.markProcessing("e-3", Instant.parse("2026-03-26T10:05:00Z"));

        JpaStoredEvent reloaded = repository.findById("e-3").orElseThrow();
        assertThat(updated).isZero();
        assertThat(reloaded.status()).isEqualTo(EventStatus.PROCESSING);
        assertThat(reloaded.startedAt()).isEqualTo(Instant.parse("2026-03-26T10:01:00Z"));
    }

    @Test
    @DisplayName("markResolved는 PROCESSING 상태를 COMPLETED로 바꾼다")
    void markResolved_to_completed() {
        Instant acceptedAt = Instant.parse("2026-03-26T10:00:00Z");

        repository.saveAndFlush(
                JpaStoredEvent.from(
                        mockHeader("seat.reserved"),
                        mockTrace("e-4", Instant.parse("2026-03-26T09:59:00Z")),
                        "{\"seatId\":\"A-1\"}",
                        EventFlow.OUTBOUND,
                        EventStatus.PROCESSING,
                        acceptedAt,
                        Instant.parse("2026-03-26T10:01:00Z"),
                        null
                )
        );

        Instant resolvedAt = Instant.parse("2026-03-26T10:07:00Z");
        int updated = repository.markResolved("e-4", EventStatus.COMPLETED, resolvedAt);

        JpaStoredEvent reloaded = repository.findById("e-4").orElseThrow();
        assertThat(updated).isEqualTo(1);
        assertThat(reloaded.status()).isEqualTo(EventStatus.COMPLETED);
        assertThat(reloaded.resolvedAt()).isEqualTo(resolvedAt);
    }

    @Test
    @DisplayName("markResolved는 PROCESSING 상태가 아니면 아무것도 하지 않는다")
    void markResolved_only_from_processing() {
        repository.saveAndFlush(stored("e-5", EventFlow.OUTBOUND, EventStatus.PENDING, Instant.parse("2026-03-26T10:00:00Z")));

        int updated = repository.markResolved(
                "e-5",
                EventStatus.FAILED,
                Instant.parse("2026-03-26T10:09:00Z")
        );

        JpaStoredEvent reloaded = repository.findById("e-5").orElseThrow();
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
