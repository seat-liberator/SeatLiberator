package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventType;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import com.seatliberator.seatliberator.eventrelay.support.jpa.EventAcceptor;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaEventStore;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Jpa Event Store")
public class JpaEventStoreTest {

    @Test
    @DisplayName("batchSize는 1보다 작을 수 없다")
    void batchSize_arg() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);

        assertThatThrownBy(() -> new JpaEventStore(repository, acceptor, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("batchSize");
    }

    @Test
    @DisplayName("accept는 acceptor에게 위임한다")
    void acceptor() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);
        JpaEventStore store = new JpaEventStore(repository, acceptor, 10);

        EventEnvelope envelope = mock(EventEnvelope.class);
        Instant acceptedAt = Instant.parse("2026-03-26T10:00:00Z");

        store.accept(envelope, EventFlow.OUTBOUND, acceptedAt);

        verify(acceptor).accept(envelope, EventFlow.OUTBOUND, acceptedAt);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("claimBatch는 markProcessing에 성공한 이벤트만 반환한다")
    void claimBatch_return_markProcessing_success_only() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);
        JpaEventStore store = new JpaEventStore(repository, acceptor, 10);

        JpaStoredEvent first = stored("e-1", EventStatus.PENDING);
        JpaStoredEvent second = stored("e-2", EventStatus.FAILED);

        when(repository.claim(anyList(), eq(EventFlow.OUTBOUND), any(Pageable.class)))
                .thenReturn(List.of(first, second));

        when(repository.markProcessing("e-1", Instant.parse("2026-03-26T10:05:00Z"))).thenReturn(1);
        when(repository.markProcessing("e-2", Instant.parse("2026-03-26T10:05:00Z"))).thenReturn(0);

        when(repository.findAllById(List.of("e-1"))).thenReturn(List.of(first));

        List<EventEnvelope> result = store.claimBatch(
                EventFlow.OUTBOUND,
                Instant.parse("2026-03-26T10:05:00Z")
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).trace().eventId()).isEqualTo("e-1");
        verify(repository).findAllById(List.of("e-1"));
    }

    @Test
    @DisplayName("claimBatch는 선점에 모두 실패하면 빈 리스트를 반환한다")
    void claimBatch_return_empty_list() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);
        JpaEventStore store = new JpaEventStore(repository, acceptor, 10);

        JpaStoredEvent first = stored("e-1", EventStatus.PENDING);

        when(repository.claim(anyList(), eq(EventFlow.OUTBOUND), any(Pageable.class)))
                .thenReturn(List.of(first));
        when(repository.markProcessing(anyString(), any())).thenReturn(0);

        List<EventEnvelope> result = store.claimBatch(
                EventFlow.OUTBOUND,
                Instant.parse("2026-03-26T10:05:00Z")
        );

        assertThat(result).isEmpty();
        verify(repository, never()).findAllById(any());
    }

    @Test
    @DisplayName("claimBatch는 batchSize로 첫 페이지를 조회한다")
    void claimBatch_find() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);
        JpaEventStore store = new JpaEventStore(repository, acceptor, 7);

        when(repository.claim(anyList(), eq(EventFlow.INBOUND), any(Pageable.class)))
                .thenReturn(List.of());

        store.claimBatch(EventFlow.INBOUND, Instant.parse("2026-03-26T10:05:00Z"));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).claim(anyList(), eq(EventFlow.INBOUND), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(7);
    }

    @Test
    @DisplayName("reportCompleted는 COMPLETED로 markResolved를 호출한다")
    void reportCompleted() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);
        JpaEventStore store = new JpaEventStore(repository, acceptor, 10);

        Instant resolvedAt = Instant.parse("2026-03-26T10:10:00Z");
        store.reportCompleted("e-1", resolvedAt);

        verify(repository).markResolved("e-1", EventStatus.COMPLETED, resolvedAt);
    }

    @Test
    @DisplayName("reportFailed는 FAILED로 markResolved를 호출한다")
    void reportFailed() {
        JpaStoredEventRepository repository = mock(JpaStoredEventRepository.class);
        EventAcceptor acceptor = mock(EventAcceptor.class);
        JpaEventStore store = new JpaEventStore(repository, acceptor, 10);

        Instant resolvedAt = Instant.parse("2026-03-26T10:11:00Z");
        store.reportFailed("e-2", resolvedAt);

        verify(repository).markResolved("e-2", EventStatus.FAILED, resolvedAt);
    }

    private JpaStoredEvent stored(String eventId, EventStatus status) {
        EventHeader header = mock(EventHeader.class);
        when(header.eventType()).thenReturn(ImmutableEventType.from("seat.reserved"));

        EventTrace trace = mock(EventTrace.class);
        when(trace.eventId()).thenReturn(eventId);
        when(trace.causationId()).thenReturn(null);
        when(trace.producer()).thenReturn("seat-service");
        when(trace.correlationId()).thenReturn("corr-" + eventId);
        when(trace.aggregateDescriptor()).thenReturn(null);
        when(trace.createdAt()).thenReturn(Instant.parse("2026-03-26T09:59:00Z"));

        return JpaStoredEvent.from(
                header,
                trace,
                "{\"seatId\":\"A-1\"}",
                EventFlow.OUTBOUND,
                status,
                Instant.parse("2026-03-26T10:00:00Z"),
                null,
                null
        );
    }
}
