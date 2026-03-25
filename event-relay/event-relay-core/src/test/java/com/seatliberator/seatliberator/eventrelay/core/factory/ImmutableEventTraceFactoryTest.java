package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventTrace;
import com.seatliberator.seatliberator.eventrelay.core.provider.CorrelationIdProvider;
import com.seatliberator.seatliberator.eventrelay.core.provider.EventIdProvider;
import com.seatliberator.seatliberator.eventrelay.core.provider.ProducerProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Immutable Event Trace Factory")
public class ImmutableEventTraceFactoryTest {

    @Test
    @DisplayName("aggregate 정보가 없으면 예외가 발생한다")
    void failWithoutAggregate() {
        var holder = new ThreadLocalEventTraceHolder();
        var factory = factory(holder);

        assertThatThrownBy(factory::create)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing aggregate descriptor");
    }

    @Test
    @DisplayName("기본 provider 값으로 EventTrace를 생성할 수 있다")
    void create() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current().withAggregate("seat", "1");

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.eventId()).isEqualTo("evt-1");
        assertThat(trace.causationId()).isNull();
        assertThat(trace.producer()).isEqualTo("seat-service");
        assertThat(trace.correlationId()).isEqualTo("corr-1");
        assertThat(trace.aggregateDescriptor().type()).isEqualTo("seat");
        assertThat(trace.aggregateDescriptor().id()).isEqualTo("1");
        assertThat(trace.createdAt()).isEqualTo(Instant.parse("2026-03-26T00:00:00Z"));
    }

    @Test
    @DisplayName("source trace가 있으면 correlation id를 상속한다")
    void inheritCorrelation() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current()
                .bindSourceTrace(sourceTrace())
                .withAggregate("seat", "1");

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.correlationId()).isEqualTo("corr-source");
    }

    @Test
    @DisplayName("source trace가 있으면 causation id로 source event id를 사용한다")
    void inheritCausation() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current()
                .bindSourceTrace(sourceTrace())
                .withAggregate("seat", "1");

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.causationId()).isEqualTo("evt-source");
    }

    @Test
    @DisplayName("correlation id override가 있으면 최우선으로 사용한다")
    void overrideCorrelation() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current()
                .bindSourceTrace(sourceTrace())
                .withAggregate("seat", "1")
                .withCorrelationId("corr-override");

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.correlationId()).isEqualTo("corr-override");
    }

    @Test
    @DisplayName("causation id override가 있으면 최우선으로 사용한다")
    void overrideCausation() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current()
                .bindSourceTrace(sourceTrace())
                .withAggregate("seat", "1")
                .withCausationId("evt-override");

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.causationId()).isEqualTo("evt-override");
    }

    @Test
    @DisplayName("correlation id 상속이 비활성화되면 provider 값을 사용한다")
    void disableCorrelationInheritance() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current()
                .bindSourceTrace(sourceTrace())
                .withAggregate("seat", "1")
                .withoutCorrelationId();

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.correlationId()).isEqualTo("corr-1");
    }

    @Test
    @DisplayName("causation id 상속이 비활성화되면 null을 사용한다")
    void disableCausationInheritance() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current()
                .bindSourceTrace(sourceTrace())
                .withAggregate("seat", "1")
                .withoutCausationId();

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.causationId()).isNull();
    }

    @Test
    @DisplayName("source trace가 없어도 correlation id는 provider로 채운다")
    void fallbackCorrelation() {
        var holder = new ThreadLocalEventTraceHolder();
        holder.current().withAggregate("seat", "1");

        var factory = factory(holder);

        var trace = factory.create();

        assertThat(trace.correlationId()).isEqualTo("corr-1");
    }

    private ImmutableEventTraceFactory factory(EventTraceHolder holder) {
        EventIdProvider eventIdProvider = mock(EventIdProvider.class);
        ProducerProvider producerProvider = mock(ProducerProvider.class);
        CorrelationIdProvider correlationIdProvider = mock(CorrelationIdProvider.class);

        given(eventIdProvider.get()).willReturn("evt-1");
        given(producerProvider.get()).willReturn("seat-service");
        given(correlationIdProvider.get()).willReturn("corr-1");

        var clock = Clock.fixed(
                Instant.parse("2026-03-26T00:00:00Z"),
                ZoneOffset.UTC
        );

        return new ImmutableEventTraceFactory(
                holder,
                eventIdProvider,
                producerProvider,
                correlationIdProvider,
                clock
        );
    }

    private ImmutableEventTrace sourceTrace() {
        return ImmutableEventTrace.from(
                "evt-source",
                "evt-parent",
                "order-service",
                "corr-source",
                ImmutableEventAggregateDescriptor.from("order", "10"),
                Instant.parse("2026-03-25T23:59:59Z")
        );
    }
}
