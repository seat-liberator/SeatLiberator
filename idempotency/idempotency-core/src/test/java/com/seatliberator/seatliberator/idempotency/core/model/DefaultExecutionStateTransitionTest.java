package com.seatliberator.seatliberator.idempotency.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DefaultExecutionStateTest - Transition")
public class DefaultExecutionStateTransitionTest {
    @Test
    @DisplayName("PENDING에서 RUNNING으로 전이하면 실행 시작 시각과 attemptCount가 갱신된다")
    void PENDING에서_RUNNING으로_전이하면_실행_시작_시각과_attemptCount가_갱신된다() {
        DefaultExecutionState state = DefaultExecutionState.pending();
        Instant tryStartAt = Instant.now();

        state.markRunning(tryStartAt);

        assertEquals(ExecutionPhase.RUNNING, state.phase());
        assertEquals(1, state.attemptCount());
        assertEquals(tryStartAt, state.tryStartAt());
        assertNull(state.resolvedAt());
        assertNull(state.output());
        assertTrue(state.isRunning());
    }

    @Test
    @DisplayName("EXECUTION_ERROR에서 RUNNING으로 재전이하면 attemptCount가 증가한다")
    void EXECUTION_ERROR에서_RUNNING으로_재전이하면_attemptCount가_증가한다() {
        DefaultExecutionState state = DefaultExecutionState.executionError(1, Instant.now());
        Instant retriedAt = Instant.now().plusSeconds(10);

        state.markRunning(retriedAt);

        assertEquals(ExecutionPhase.RUNNING, state.phase());
        assertEquals(2, state.attemptCount());
        assertEquals(retriedAt, state.tryStartAt());
        assertNull(state.resolvedAt());
        assertNull(state.output());
    }

    @Test
    @DisplayName("RUNNING에서 RESOLVED로 전이하면 resolvedAt과 output이 기록된다")
    void RUNNING에서_RESOLVED로_전이하면_resolvedAt과_output이_기록된다() {
        DefaultExecutionState state = DefaultExecutionState.running(1, Instant.now());
        Instant resolvedAt = Instant.now().plusSeconds(1);
        ExecutionOutput output = ImmutableExecutionOutput.success("ok");

        state.markResolved(resolvedAt, output);

        assertEquals(ExecutionPhase.RESOLVED, state.phase());
        assertEquals(1, state.attemptCount());
        assertNotNull(state.tryStartAt());
        assertEquals(resolvedAt, state.resolvedAt());
        assertNotNull(state.output());
        assertTrue(state.output().hasResult());
        assertFalse(state.output().hasError());
        assertTrue(state.isResolved());
    }

    @Test
    @DisplayName("RUNNING에서 EXECUTION_ERROR로 전이하면 resolvedAt과 output은 비워진다")
    void RUNNING에서_EXECUTION_ERROR로_전이하면_resolvedAt과_output은_비워진다() {
        DefaultExecutionState state = DefaultExecutionState.running(1, Instant.now());

        state.markExecutionError();

        assertEquals(ExecutionPhase.EXECUTION_ERROR, state.phase());
        assertEquals(1, state.attemptCount());
        assertNotNull(state.tryStartAt());
        assertNull(state.resolvedAt());
        assertNull(state.output());
        assertTrue(state.isExecutionError());
    }


    @Test
    @DisplayName("RUNNING이 아닌 상태에서는 RESOLVED로 전이할 수 없다")
    void RUNNING이_아닌_상태에서는_RESOLVED로_전이할_수_없다() {
        DefaultExecutionState state = DefaultExecutionState.pending();

        assertThrows(IllegalStateException.class, () ->
                state.markResolved(
                        Instant.now(),
                        ImmutableExecutionOutput.success("ok")
                )
        );
    }

    @Test
    @DisplayName("RUNNING이 아닌 상태에서는 EXECUTION_ERROR로 전이할 수 없다")
    void RUNNING이_아닌_상태에서는_EXECUTION_ERROR로_전이할_수_없다() {
        DefaultExecutionState state = DefaultExecutionState.pending();

        assertThrows(IllegalStateException.class, state::markExecutionError);
    }

    @Test
    @DisplayName("RUNNING으로 전이하면 이전 resolved 이력은 초기화된다")
    void RUNNING으로_전이하면_이전_resolved_이력은_초기화된다() {
        DefaultExecutionState state = DefaultExecutionState.executionError(1, Instant.now());
        Instant retriedAt = Instant.now().plusSeconds(5);

        state.markRunning(retriedAt);

        assertNull(state.resolvedAt());
        assertNull(state.output());
    }
}
