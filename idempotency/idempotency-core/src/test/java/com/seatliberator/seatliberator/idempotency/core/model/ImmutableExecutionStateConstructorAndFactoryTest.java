package com.seatliberator.seatliberator.idempotency.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImmutableExecutionState - Constructor / Factory")
public class ImmutableExecutionStateConstructorAndFactoryTest {
    @Test
    @DisplayName("pending은 유효한 PENDING 상태를 생성한다")
    void pending은_유효한_PENDING_상태를_생성한다() {
        ExecutionState state = ImmutableExecutionState.pending();

        assertEquals(ExecutionPhase.PENDING, state.phase());
        assertEquals(0, state.attemptCount());
        assertNull(state.tryStartAt());
        assertNull(state.resolvedAt());
        assertNull(state.output());
        assertTrue(state.isPending());
        assertFalse(state.isRunning());
        assertFalse(state.isResolved());
        assertFalse(state.isExecutionError());
    }


    @Test
    @DisplayName("of는 기존 ExecutionState를 DefaultExecutionState로 복사한다")
    void of는_기존_ExecutionState를_DefaultExecutionState로_복사한다() {
        Instant tryStartAt = Instant.now();
        Instant resolvedAt = tryStartAt.plusSeconds(1);
        ExecutionOutput output = ImmutableExecutionOutput.failure(new RuntimeException("boom"));

        ExecutionState source = ImmutableExecutionState.resolved(
                2,
                tryStartAt,
                resolvedAt,
                output
        );

        ImmutableExecutionState copied = ImmutableExecutionState.of(source);

        assertEquals(source.phase(), copied.phase());
        assertEquals(source.attemptCount(), copied.attemptCount());
        assertEquals(source.tryStartAt(), copied.tryStartAt());
        assertEquals(source.resolvedAt(), copied.resolvedAt());
        assertNotNull(copied.output());
        assertEquals(source.output().hasResult(), copied.output().hasResult());
        assertEquals(source.output().hasError(), copied.output().hasError());
    }

    @Test
    @DisplayName("PENDING 상태는 attemptCount가 0이어야 한다")
    void PENDING_상태는_attemptCount가_0이어야_한다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.PENDING,
                        1,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("PENDING 상태는 tryStartAt이 있으면 안 된다")
    void PENDING_상태는_tryStartAt이_있으면_안된다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.PENDING,
                        0,
                        Instant.now(),
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("RUNNING 상태는 tryStartAt이 반드시 있어야 한다")
    void RUNNING_상태는_tryStartAt이_반드시_있어야_한다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.RUNNING,
                        1,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("RUNNING 상태는 output이 있으면 안 된다")
    void RUNNING_상태는_output이_있으면_안된다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.RUNNING,
                        1,
                        Instant.now(),
                        null,
                        ImmutableExecutionOutput.success("ok")
                )
        );
    }

    @Test
    @DisplayName("RESOLVED 상태는 resolvedAt이 반드시 있어야 한다")
    void RESOLVED_상태는_resolvedAt이_반드시_있어야_한다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.RESOLVED,
                        1,
                        Instant.now(),
                        null,
                        ImmutableExecutionOutput.success("ok")
                )
        );
    }

    @Test
    @DisplayName("RESOLVED 상태는 output이 반드시 있어야 한다")
    void RESOLVED_상태는_output이_반드시_있어야_한다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.RESOLVED,
                        1,
                        Instant.now(),
                        Instant.now().plusSeconds(1),
                        null
                )
        );
    }

    @Test
    @DisplayName("RESOLVED 상태는 resolvedAt이 tryStartAt보다 빠를 수 없다")
    void RESOLVED_상태는_resolvedAt이_tryStartAt보다_빠를_수_없다() {
        Instant tryStartAt = Instant.now();
        Instant resolvedAt = tryStartAt.minusSeconds(1);

        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.RESOLVED,
                        1,
                        tryStartAt,
                        resolvedAt,
                        ImmutableExecutionOutput.success("ok")
                )
        );
    }

    @Test
    @DisplayName("EXECUTION_ERROR 상태는 tryStartAt이 반드시 있어야 한다")
    void EXECUTION_ERROR_상태는_tryStartAt이_반드시_있어야_한다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.EXECUTION_ERROR,
                        1,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("EXECUTION_ERROR 상태는 output이 있으면 안 된다")
    void EXECUTION_ERROR_상태는_output이_있으면_안된다() {
        assertThrows(IllegalArgumentException.class, () ->
                new ImmutableExecutionState(
                        ExecutionPhase.EXECUTION_ERROR,
                        1,
                        Instant.now(),
                        null,
                        ImmutableExecutionOutput.failure(new RuntimeException("boom"))
                )
        );
    }
}
