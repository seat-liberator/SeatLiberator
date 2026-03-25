package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventTrace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Default Event Trace State")
public class DefaultEventTraceStateTest {

    @Test
    @DisplayName("source trace를 바인딩할 수 있다")
    void bindSource() {
        var state = new DefaultEventTraceState();
        var sourceTrace = ImmutableEventTrace.from(
                "event-1",
                null,
                "reservation-service",
                "correlation-1",
                ImmutableEventAggregateDescriptor.from("reservation", "1"),
                Instant.parse("2026-01-01T00:00:00Z")
        );

        state.bindSourceTrace(sourceTrace);

        assertThat(state.sourceTrace()).isNotNull();
        assertThat(state.sourceTrace().eventId()).isEqualTo("event-1");
        assertThat(state.sourceTrace().correlationId()).isEqualTo("correlation-1");
        assertThat(state.sourceTrace()).isNotSameAs(sourceTrace);
    }

    @Test
    @DisplayName("aggregate 정보를 설정할 수 있다")
    void withAggregate() {
        var state = new DefaultEventTraceState();

        state.withAggregate("seat", "1");

        assertThat(state.aggregateDescriptor()).isNotNull();
        assertThat(state.aggregateDescriptor().type()).isEqualTo("seat");
        assertThat(state.aggregateDescriptor().id()).isEqualTo("1");
    }

    @Test
    @DisplayName("correlation id를 override할 수 있다")
    void withCorrelation() {
        var state = new DefaultEventTraceState();

        state.withCorrelationId("corr-1");

        assertThat(state.overrideCorrelationId()).isEqualTo("corr-1");
        assertThat(state.disableInheritCorrelationId()).isTrue();
    }

    @Test
    @DisplayName("correlation id 상속을 비활성화할 수 있다")
    void withoutCorrelation() {
        var state = new DefaultEventTraceState();

        state.withoutCorrelationId();

        assertThat(state.overrideCorrelationId()).isNull();
        assertThat(state.disableInheritCorrelationId()).isTrue();
    }

    @Test
    @DisplayName("causation id를 override할 수 있다")
    void withCausation() {
        var state = new DefaultEventTraceState();

        state.withCausationId("evt-0");

        assertThat(state.overrideCausationId()).isEqualTo("evt-0");
        assertThat(state.disableInheritCausationId()).isTrue();
    }

    @Test
    @DisplayName("causation id 상속을 비활성화할 수 있다")
    void withoutCausation() {
        var state = new DefaultEventTraceState();

        state.withoutCausationId();

        assertThat(state.overrideCausationId()).isNull();
        assertThat(state.disableInheritCausationId()).isTrue();
    }

    @Test
    @DisplayName("override 상태를 초기화할 수 있다")
    void clearOverrides() {
        var state = new DefaultEventTraceState();
        var sourceTrace = ImmutableEventTrace.from(
                "evt-1",
                "evt-0",
                "seat-service",
                "corr-1",
                ImmutableEventAggregateDescriptor.from("seat", "1"),
                Instant.parse("2026-03-26T00:00:00Z")
        );

        state.bindSourceTrace(sourceTrace);
        state.withAggregate("reservation", "10");
        state.withCorrelationId("corr-2");
        state.withCausationId("evt-9");

        state.clearOverrides();

        assertThat(state.sourceTrace()).isNotNull();
        assertThat(state.aggregateDescriptor()).isNull();
        assertThat(state.overrideCorrelationId()).isNull();
        assertThat(state.overrideCausationId()).isNull();
        assertThat(state.disableInheritCorrelationId()).isFalse();
        assertThat(state.disableInheritCausationId()).isFalse();
    }

    @Test
    @DisplayName("현재 상태를 복사할 수 있다")
    void copy() {
        var state = new DefaultEventTraceState();
        var sourceTrace = ImmutableEventTrace.from(
                "evt-1",
                "evt-0",
                "seat-service",
                "corr-1",
                ImmutableEventAggregateDescriptor.from("seat", "1"),
                Instant.parse("2026-03-26T00:00:00Z")
        );

        state.bindSourceTrace(sourceTrace);
        state.withAggregate("reservation", "10");
        state.withCorrelationId("corr-2");
        state.withCausationId("evt-9");

        var copied = state.copy();

        assertThat(copied).isNotSameAs(state);
        assertThat(copied.sourceTrace()).isNotNull();
        assertThat(copied.sourceTrace()).isNotSameAs(state.sourceTrace());
        assertThat(copied.sourceTrace().eventId()).isEqualTo("evt-1");
        assertThat(copied.aggregateDescriptor()).isNotNull();
        assertThat(copied.aggregateDescriptor().type()).isEqualTo("reservation");
        assertThat(copied.aggregateDescriptor().id()).isEqualTo("10");
        assertThat(copied.overrideCorrelationId()).isEqualTo("corr-2");
        assertThat(copied.overrideCausationId()).isEqualTo("evt-9");
        assertThat(copied.disableInheritCorrelationId()).isTrue();
        assertThat(copied.disableInheritCausationId()).isTrue();
    }

    @Test
    @DisplayName("다른 상태로 복원할 수 있다")
    void restore() {
        var state = new DefaultEventTraceState();
        var source = new DefaultEventTraceState();

        var sourceTrace = ImmutableEventTrace.from(
                "evt-1",
                "evt-0",
                "seat-service",
                "corr-1",
                ImmutableEventAggregateDescriptor.from("seat", "1"),
                Instant.parse("2026-03-26T00:00:00Z")
        );

        source.bindSourceTrace(sourceTrace);
        source.withAggregate("reservation", "10");
        source.withCorrelationId("corr-2");
        source.withCausationId("evt-9");

        state.restore(source);

        assertThat(state.sourceTrace()).isNotNull();
        assertThat(state.sourceTrace()).isNotSameAs(source.sourceTrace());
        assertThat(state.sourceTrace().eventId()).isEqualTo("evt-1");
        assertThat(state.aggregateDescriptor()).isNotNull();
        assertThat(state.aggregateDescriptor()).isNotSameAs(source.aggregateDescriptor());
        assertThat(state.aggregateDescriptor().type()).isEqualTo("reservation");
        assertThat(state.aggregateDescriptor().id()).isEqualTo("10");
        assertThat(state.overrideCorrelationId()).isEqualTo("corr-2");
        assertThat(state.overrideCausationId()).isEqualTo("evt-9");
        assertThat(state.disableInheritCorrelationId()).isTrue();
        assertThat(state.disableInheritCausationId()).isTrue();
    }
}
