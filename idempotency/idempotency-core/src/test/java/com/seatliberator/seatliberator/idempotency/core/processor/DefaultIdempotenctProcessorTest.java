package com.seatliberator.seatliberator.idempotency.core.processor;

import com.seatliberator.seatliberator.idempotency.core.decision.Decision;
import com.seatliberator.seatliberator.idempotency.core.decision.ExecutionDecision;
import com.seatliberator.seatliberator.idempotency.core.decision.ExecutionDecisionEngine;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionAttemptLimitExceededException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionContextMismatchException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionPendingException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionTimeoutException;
import com.seatliberator.seatliberator.idempotency.core.model.ExecutionOutput;
import com.seatliberator.seatliberator.idempotency.core.model.ExecutionState;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyState;
import com.seatliberator.seatliberator.idempotency.core.model.ImmutableExecutionOutput;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyRecord;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static com.seatliberator.seatliberator.idempotency.core.util.IdempotencyTestUtil.context;
import static com.seatliberator.seatliberator.idempotency.core.util.IdempotencyTestUtil.key;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("DefaultIdempotencyProcessor")
public class DefaultIdempotenctProcessorTest {

    private IdempotencyStore store;
    private ExecutionDecisionEngine engine;
    private DefaultIdempotentProcessor processor;

    @BeforeEach
    void setup() {
        store = mock(IdempotencyStore.class);
        engine = mock(ExecutionDecisionEngine.class);
        processor = new DefaultIdempotentProcessor(store, engine);
    }

    @Test
    @DisplayName("EXECUTE 결정이면 action을 실행하고 성공 결과를 반환한다")
    void EXECUTE_결정이면_action을_실행하고_성공_결과를_반환한다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, true);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx)).thenReturn(ExecutionDecision.execute());
        when(store.tryMarkRunning(key)).thenReturn(true);
        when(store.tryMarkResolved(eq(key), any())).thenReturn(true);

        String result = processor.process(key, ctx, () -> "OK");

        assertThat(result).isEqualTo("OK");

        ArgumentCaptor<ExecutionOutput> captor = ArgumentCaptor.forClass(ExecutionOutput.class);
        verify(store).tryMarkResolved(eq(key), captor.capture());

        assertThat(captor.getValue().hasError()).isFalse();
        assertThat(captor.getValue().result()).isEqualTo("OK");
    }

    @Test
    @DisplayName("EXECUTE 결정이고 action이 예외를 던지면 failure output으로 저장 후 예외를 던진다")
    void EXECUTE_결정이고_action이_예외를_던지면_failure_output으로_저장_후_예외를_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, true);
        var exception = new IllegalArgumentException("boom");

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx)).thenReturn(ExecutionDecision.execute());
        when(store.tryMarkRunning(key)).thenReturn(true);
        when(store.tryMarkResolved(eq(key), any())).thenReturn(true);

        assertThatThrownBy(() -> processor.process(key, ctx, () -> {
            throw exception;
        })).isSameAs(exception);

        ArgumentCaptor<ExecutionOutput> captor = ArgumentCaptor.forClass(ExecutionOutput.class);
        verify(store).tryMarkResolved(eq(key), captor.capture());

        assertThat(captor.getValue().hasError()).isTrue();
        assertThat(captor.getValue().error()).isSameAs(exception);
    }

    @Test
    @DisplayName("EXECUTE 결정이고 action이 InterruptedException을 던지면 resolved 저장 없이 interrupt 상태를 복원하고 pending 예외를 던진다")
    void EXECUTE_결정이고_action이_InterruptedException을_던지면_resolved_저장_없이_interrupt_상태를_복원하고_pending_예외를_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, true);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx)).thenReturn(ExecutionDecision.execute());
        when(store.tryMarkRunning(key)).thenReturn(true);

        try {
            assertThatThrownBy(() -> processor.process(key, ctx, () -> {
                throw new InterruptedException("interrupted");
            })).isInstanceOf(ExecutionPendingException.class);

            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            verify(store, never()).tryMarkResolved(any(), any());
        } finally {
            Thread.interrupted(); // 다음 테스트에 영향 없도록 clear
        }
    }

    @Test
    @DisplayName("RUNNING 전이에 실패하면 pending 예외를 던진다")
    void RUNNING_전이에_실패하면_pending_예외를_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, true);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx)).thenReturn(ExecutionDecision.execute());
        when(store.tryMarkRunning(key)).thenReturn(false);

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "OK"))
                .isInstanceOf(ExecutionPendingException.class);

        verify(store, never()).tryMarkResolved(any(), any());
    }

    @Test
    @DisplayName("REUSE_RESOLVED_RESULT 이고 success output이면 저장된 결과를 반환한다")
    void REUSE_RESOLVED_RESULT_이고_success_output이_저장된_결과를_반환한다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var executionState = mock(ExecutionState.class);
        var executionOutput = ImmutableExecutionOutput.success("CACHED");

        when(state.executionState()).thenReturn(executionState);
        when(executionState.output()).thenReturn(executionOutput);
        when(state.createdAt()).thenReturn(Instant.now());

        var record = new IdempotencyRecord(state, false);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.skip(Decision.REUSE_RESOLVED_RESULT));

        String result = processor.process(key, ctx, () -> "NEW");

        assertThat(result).isEqualTo("CACHED");
        verify(store, never()).tryMarkRunning(any());
    }

    @Test
    @DisplayName("REUSE_RESOLVED_RESULT 이고 failure output이면 저장된 예외를 반환한다")
    void REUSE_RESOLVED_RESULT_이고_failure_output이_저장된_예외를_반환한다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var executionState = mock(ExecutionState.class);
        var exception = new RuntimeException("boom");
        var executionOutput = ImmutableExecutionOutput.failure(exception);

        when(state.executionState()).thenReturn(executionState);
        when(executionState.output()).thenReturn(executionOutput);
        when(state.createdAt()).thenReturn(Instant.now());

        var record = new IdempotencyRecord(state, false);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.skip(Decision.REUSE_RESOLVED_RESULT));

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "NEW")).isSameAs(exception);
    }

    @Test
    @DisplayName("REUSE_RESOLVED_RESULT 이고 checked failure output이면 저장된 checked 예외를 그대로 다시 던진다")
    void REUSE_RESOLVED_RESULT_이고_checked_failure_output이면_저장된_checked_예외를_그대로_다시_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var executionState = mock(ExecutionState.class);
        var exception = new java.io.IOException("checked boom");
        var executionOutput = ImmutableExecutionOutput.failure(exception);

        when(state.executionState()).thenReturn(executionState);
        when(executionState.output()).thenReturn(executionOutput);
        when(state.createdAt()).thenReturn(Instant.now());

        var record = new IdempotencyRecord(state, false);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.skip(Decision.REUSE_RESOLVED_RESULT));

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "NEW"))
                .isSameAs(exception);
    }

    @Test
    @DisplayName("EXECUTE 결정이고 action이 checked 예외를 던지면 failure output으로 저장 후 같은 예외를 던진다")
    void EXECUTE_결정이고_action이_checked_예외를_던지면_failure_output으로_저장_후_같은_예외를_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, true);
        var exception = new java.io.IOException("checked boom");

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx)).thenReturn(ExecutionDecision.execute());
        when(store.tryMarkRunning(key)).thenReturn(true);
        when(store.tryMarkResolved(eq(key), any())).thenReturn(true);

        assertThatThrownBy(() -> processor.process(key, ctx, () -> {
            throw exception;
        })).isSameAs(exception);

        ArgumentCaptor<ExecutionOutput> captor = ArgumentCaptor.forClass(ExecutionOutput.class);
        verify(store).tryMarkResolved(eq(key), captor.capture());

        assertThat(captor.getValue().hasError()).isTrue();
        assertThat(captor.getValue().error()).isSameAs(exception);
    }

    @Test
    @DisplayName("REUSE_RUNNING_STATE 이면 pending 예외를 던진다")
    void REUSE_RUNNING_STATE_이면_pending_예외를_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, false);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.skip(Decision.REUSE_RUNNING_STATE));

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "OK"))
                .isInstanceOf(ExecutionPendingException.class);
    }

    @Test
    @DisplayName("Context mismatch reject 이면 decision throwable 을 다시 던진다")
    void Context_mismatch_reject_이면_decision_throwable_을_다시_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-2");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, false);
        var exception = new ExecutionContextMismatchException("user-1", ctx.fingerprint());

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.reject(Decision.REJECT_CONTEXT_MISMATCH, exception));

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "OK"))
                .isSameAs(exception);
    }

    @Test
    @DisplayName("attempt limit reject 이면 decision throwable 을 다시 던진다")
    void attempt_limit_reject_이면_decision_throwable_을_다시_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-2");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, false);
        var exception = new ExecutionAttemptLimitExceededException(5, 5);

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.reject(Decision.REJECT_ATTEMPT_LIMIT_EXCEEDED, exception));

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "OK"))
                .isSameAs(exception);
    }

    @Test
    @DisplayName("timeout reject 이면 decision throwable 을 다시 던진다")
    void timeout_reject_이면_decision_throwable_을_다시_던진다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");
        var state = mock(IdempotencyState.class);
        var record = new IdempotencyRecord(state, false);
        var exception = new ExecutionTimeoutException(Instant.now(), Instant.now().plusSeconds(30));

        when(store.getOrCreate(key, ctx)).thenReturn(record);
        when(engine.decide(state, ctx))
                .thenReturn(ExecutionDecision.reject(Decision.REJECT_EXECUTION_TIMEOUT, exception));

        assertThatThrownBy(() -> processor.process(key, ctx, () -> "OK"))
                .isSameAs(exception);
    }
}
