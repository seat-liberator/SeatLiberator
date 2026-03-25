package com.seatliberator.seatliberator.eventrelay.core.factory;

import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventAggregateDescriptor;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventTrace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Thread Local Event Trace Holder")
public class ThreadLocalEventTraceHolderTest {
    @Test
    @DisplayName("현재 상태를 조회할 수 있다")
    void current() {
        var holder = new ThreadLocalEventTraceHolder();

        var current = holder.current();

        assertThat(current).isNotNull();
        assertThat(current.sourceTrace()).isNull();
    }

    @Test
    @DisplayName("with 범위에서 상태를 바인딩할 수 있다")
    void with() {
        var holder = new ThreadLocalEventTraceHolder();

        holder.with(() -> {
            var current = holder.current();

            assertThat(current.aggregateDescriptor()).isNotNull();
            assertThat(current.aggregateDescriptor().type()).isEqualTo("seat");
            assertThat(current.aggregateDescriptor().id()).isEqualTo("1");
        }, state -> state.withAggregate("seat", "1"));
    }

    @Test
    @DisplayName("with 종료 후 이전 상태로 복원된다")
    void restoreAfterWith() {
        var holder = new ThreadLocalEventTraceHolder();

        holder.current().withAggregate("reservation", "10");

        holder.with(() -> {
            var current = holder.current();

            assertThat(current.aggregateDescriptor().type()).isEqualTo("seat");
        }, state -> state.withAggregate("seat", "1"));

        var current = holder.current();

        assertThat(current.aggregateDescriptor().type()).isEqualTo("reservation");
    }

    @Test
    @DisplayName("with 내부에서 예외가 발생해도 상태가 복원된다")
    void restoreAfterError() {
        var holder = new ThreadLocalEventTraceHolder();

        holder.current().withAggregate("reservation", "10");

        assertThatThrownBy(() ->
                holder.with(() -> {
                    throw new IllegalStateException("fail");
                }, state -> state.withAggregate("seat", "1"))
        ).isInstanceOf(IllegalStateException.class);

        var current = holder.current();

        assertThat(current.aggregateDescriptor().type()).isEqualTo("reservation");
    }

    @Test
    @DisplayName("중첩된 with 범위가 종료되면 바깥 상태로 복원된다")
    void nestedWith() {
        var holder = new ThreadLocalEventTraceHolder();

        holder.with(() -> {
            holder.with(() -> {
                var inner = holder.current();
                assertThat(inner.aggregateDescriptor().type()).isEqualTo("inner");
            }, s -> s.withAggregate("inner", "2"));

            var restored = holder.current();
            assertThat(restored.aggregateDescriptor().type()).isEqualTo("outer");
        }, s -> s.withAggregate("outer", "1"));
    }

    @Test
    @DisplayName("withSource 범위에서 source trace를 바인딩할 수 있다")
    void withSource() {
        var holder = new ThreadLocalEventTraceHolder();
        var sourceTrace = sampleTrace();

        holder.withSource(() -> {
            var current = holder.current();

            assertThat(current.sourceTrace()).isNotNull();
            assertThat(current.sourceTrace().eventId()).isEqualTo("evt-1");
        }, sourceTrace);
    }

    @Test
    @DisplayName("withSource는 기존 override 상태를 초기화한다")
    void clearOverridesInWithSource() {
        var holder = new ThreadLocalEventTraceHolder();
        var sourceTrace = sampleTrace();

        holder.current().withAggregate("reservation", "10");
        holder.current().withCorrelationId("corr-9");

        holder.withSource(() -> {
            var current = holder.current();

            assertThat(current.aggregateDescriptor()).isNull();
            assertThat(current.overrideCorrelationId()).isNull();
        }, sourceTrace);
    }

    @Test
    @DisplayName("withSource 종료 후 이전 상태로 복원된다")
    void restoreAfterWithSource() {
        var holder = new ThreadLocalEventTraceHolder();
        var sourceTrace = sampleTrace();

        holder.current().withAggregate("reservation", "10");

        holder.withSource(() -> {
            assertThat(holder.current().sourceTrace()).isNotNull();
        }, sourceTrace);

        var current = holder.current();

        assertThat(current.sourceTrace()).isNull();
        assertThat(current.aggregateDescriptor().type()).isEqualTo("reservation");
    }

    @Test
    @DisplayName("clear로 상태를 초기화할 수 있다")
    void clear() {
        var holder = new ThreadLocalEventTraceHolder();

        holder.current().withAggregate("reservation", "10");

        holder.clear();

        var current = holder.current();

        assertThat(current.aggregateDescriptor()).isNull();
    }

    private ImmutableEventTrace sampleTrace() {
        return ImmutableEventTrace.from(
                "evt-1",
                "evt-0",
                "seat-service",
                "corr-1",
                ImmutableEventAggregateDescriptor.from("seat", "1"),
                Instant.parse("2026-03-26T00:00:00Z")
        );
    }
}
