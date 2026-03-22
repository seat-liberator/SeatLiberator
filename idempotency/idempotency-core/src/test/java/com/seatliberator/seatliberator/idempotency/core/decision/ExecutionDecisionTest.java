package com.seatliberator.seatliberator.idempotency.core.decision;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExecutionDecision")
public class ExecutionDecisionTest {
    @Test
    @DisplayName("execute는 실행 가능한 EXECUTE 판단을 생성한다")
    void execute는_실행_가능한_EXECUTE_판단을_생성한다() {
        ExecutionDecision decision = ExecutionDecision.execute();

        assertAll(
                () -> assertTrue(decision.shouldExecute()),
                () -> assertEquals(Decision.EXECUTE, decision.kind()),
                () -> assertNull(decision.throwable())
        );
    }

    @Test
    @DisplayName("skip은 비실행 판단을 생성한다")
    void skip은_비실행_판단을_생성한다() {
        ExecutionDecision decision = ExecutionDecision.skip(Decision.REUSE_RUNNING_STATE);

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REUSE_RUNNING_STATE, decision.kind()),
                () -> assertNull(decision.throwable())
        );
    }

    @Test
    @DisplayName("reject는 예외를 포함한 비실행 판단을 생성한다")
    void reject는_예외를_포함한_비실행_판단을_생성한다() {
        RuntimeException throwable = new RuntimeException("boom");

        ExecutionDecision decision = ExecutionDecision.reject(
                Decision.REJECT_EXECUTION_TIMEOUT,
                throwable
        );

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REJECT_EXECUTION_TIMEOUT, decision.kind()),
                () -> assertSame(throwable, decision.throwable())
        );
    }

    @Test
    @DisplayName("shouldExecute가 true인데 throwable이 있으면 예외를 던진다")
    void shouldExecute가_true인데_throwable이_있으면_예외를_던진다() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ExecutionDecision(
                        true,
                        Decision.EXECUTE,
                        new RuntimeException("boom")
                )
        );
    }

    @Test
    @DisplayName("shouldExecute가 true인데 kind가 EXECUTE가 아니면 예외를 던진다")
    void shouldExecute가_true인데_kind가_EXECUTE가_아니면_예외를_던진다() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ExecutionDecision(
                        true,
                        Decision.REUSE_RUNNING_STATE,
                        null
                )
        );
    }

    @Test
    @DisplayName("shouldExecute가 false인데 throwable 없이 kind가 EXECUTE면 예외를 던진다")
    void shouldExecute가_false인데_throwable_없이_kind가_EXECUTE면_예외를_던진다() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ExecutionDecision(
                        false,
                        Decision.EXECUTE,
                        null
                )
        );
    }
}
