package com.seatliberator.seatliberator.idempotency.core.decision;

import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionAttemptLimitExceededException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionContextMismatchException;
import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionTimeoutException;
import com.seatliberator.seatliberator.idempotency.core.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DefaultExecutionDecisionEngine")
public class DefaultExecutionDecisionEngineTest {

    private static Clock fixedClock(String instant) {
        return Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
    }

    private static IdempotencyState state(
            ExecutionState executionState,
            IdempotencyContext context
    ) {
        return new ImmutableIdempotencyState(
                new ImmutableIdempotencyKey("source-key", "operation-key"),
                ImmutableIdempotencyContext.of(context),
                ImmutableExecutionState.of(executionState),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }

    private static IdempotencyContext context(
            String fingerprint
    ) {
        return new ImmutableIdempotencyContext(fingerprint);
    }

    private static DefaultExecutionDecisionEngine engine(
            int maxAttemptCount,
            int timeout,
            Clock clock
    ) {
        return new DefaultExecutionDecisionEngine(
                maxAttemptCount,
                Duration.ofSeconds(timeout),
                clock
        );
    }

    @Test
    @DisplayName("context가 다르면 REJECT_CONTEXT_MISMATCH를 반환한다")
    void context가_다르면_REJECT_CONTEXT_MISMATCH를_반환한다() {
        Clock nowClock = fixedClock("2026-01-01T00:00:20Z");
        DefaultExecutionDecisionEngine engine = engine(3, 30, nowClock);

        IdempotencyState state = state(
                DefaultExecutionState.pending(),
                context("original-fp")
        );

        ExecutionDecision decision = engine.decide(state, context("other-fp"));

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REJECT_CONTEXT_MISMATCH, decision.kind()),
                () -> assertInstanceOf(ExecutionContextMismatchException.class, decision.throwable())
        );
    }

    @Test
    @DisplayName("RUNNING이고 timeout 이전이면 REUSE_RUNNING_STATE를 반환한다")
    void running이고_timeout_이전이면_REUSE_RUNNING_STATE를_반환한다() {
        Instant tryStartAt = Instant.parse("2026-01-01T00:00:00Z");
        Clock nowClock = fixedClock("2026-01-01T00:00:20Z");
        DefaultExecutionDecisionEngine engine = engine(3, 30, nowClock);
        var originalCtx = context("original-fp");

        IdempotencyState state = state(
                DefaultExecutionState.running(1, tryStartAt),
                originalCtx
        );

        ExecutionDecision decision = engine.decide(state, originalCtx);

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REUSE_RUNNING_STATE, decision.kind()),
                () -> assertNull(decision.throwable())
        );
    }

    @Test
    @DisplayName("RUNNING이고 timeout 초과면 REJECT_EXECUTION_TIMEOUT을 반환한다")
    void running이고_timeout_초과면_REJECT_EXECUTION_TIMEOUT을_반환한다() {
        Instant tryStartAt = Instant.parse("2026-01-01T00:00:00Z");
        Clock nowClock = fixedClock("2026-01-01T00:00:31Z");
        DefaultExecutionDecisionEngine engine = engine(3, 30, nowClock);
        var originalCtx = context("original-fp");

        IdempotencyState state = state(
                DefaultExecutionState.running(1, tryStartAt),
                originalCtx
        );

        ExecutionDecision decision = engine.decide(state, originalCtx);

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REJECT_EXECUTION_TIMEOUT, decision.kind()),
                () -> assertInstanceOf(ExecutionTimeoutException.class, decision.throwable())
        );
    }

    @Test
    @DisplayName("RESOLVED면 REUSE_RESOLVED_RESULT를 반환한다")
    void resolved면_REUSE_RESOLVED_RESULT를_반환한다() {
        Instant tryStartAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant resolvedAt = tryStartAt.plusSeconds(5);
        Clock nowClock = fixedClock("2026-01-01T00:00:31Z");
        DefaultExecutionDecisionEngine engine = engine(3, 30, nowClock);
        var originalCtx = context("original-fp");

        ImmutableExecutionOutput output = ImmutableExecutionOutput.success("ok");
        IdempotencyState state = state(
                DefaultExecutionState.resolved(1, tryStartAt, resolvedAt, output),
                originalCtx
        );

        ExecutionDecision decision = engine.decide(state, originalCtx);

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REUSE_RESOLVED_RESULT, decision.kind()),
                () -> assertNull(decision.throwable())
        );
    }

    @Test
    @DisplayName("EXECUTION_ERROR이고 attemptCount가 제한 미만이면 EXECUTE를 반환한다")
    void execution_error이고_attempt가_제한_미만이면_EXECUTE를_반환한다() {
        Instant tryStartAt = Instant.parse("2026-01-01T00:00:00Z");
        Clock nowClock = fixedClock("2026-01-01T00:00:31Z");
        DefaultExecutionDecisionEngine engine = engine(3, 30, nowClock);
        var originalCtx = context("original-fp");

        IdempotencyState state = state(
                DefaultExecutionState.executionError(1, tryStartAt),
                originalCtx
        );

        ExecutionDecision decision = engine.decide(state, originalCtx);

        assertAll(
                () -> assertTrue(decision.shouldExecute()),
                () -> assertEquals(Decision.EXECUTE, decision.kind()),
                () -> assertNull(decision.throwable())
        );
    }

    @Test
    @DisplayName("EXECUTION_ERROR이고 attemptCount가 제한 이상이면 REJECT_ATTEMPT_LIMIT_EXCEEDED를 반환한다")
    void execution_error이고_attempt가_제한_이상이면_REJECT_ATTEMPT_LIMIT_EXCEEDED를_반환한다() {
        Instant tryStartAt = Instant.parse("2026-01-01T00:00:00Z");
        Clock nowClock = fixedClock("2026-01-01T00:00:31Z");
        int maxAttemptCount = 3;
        int timeout = 30;
        DefaultExecutionDecisionEngine engine = engine(maxAttemptCount, timeout, nowClock);
        var originalCtx = context("original-fp");

        IdempotencyState state = state(
                DefaultExecutionState.executionError(maxAttemptCount, tryStartAt),
                originalCtx
        );

        ExecutionDecision decision = engine.decide(state, originalCtx);

        assertAll(
                () -> assertFalse(decision.shouldExecute()),
                () -> assertEquals(Decision.REJECT_ATTEMPT_LIMIT_EXCEEDED, decision.kind()),
                () -> assertInstanceOf(ExecutionAttemptLimitExceededException.class, decision.throwable())
        );
    }

    @Test
    @DisplayName("PENDING이면 EXECUTE를 반환한다")
    void pending이면_EXECUTE를_반환한다() {
        Clock nowClock = fixedClock("2026-01-01T00:00:31Z");
        int maxAttemptCount = 3;
        int timeout = 30;
        DefaultExecutionDecisionEngine engine = engine(maxAttemptCount, timeout, nowClock);
        var originalCtx = context("original-fp");

        IdempotencyState state = state(
                DefaultExecutionState.pending(),
                originalCtx
        );

        ExecutionDecision decision = engine.decide(state, originalCtx);

        assertAll(
                () -> assertTrue(decision.shouldExecute()),
                () -> assertEquals(Decision.EXECUTE, decision.kind()),
                () -> assertNull(decision.throwable())
        );
    }

    @Test
    @DisplayName("maxAttemptCount가 1보다 작으면 예외를 던진다")
    void maxAttemptCount가_1보다_작으면_예외를_던진다() {
        Clock nowClock = fixedClock("2026-01-01T00:00:31Z");
        int timeout = 30;
        assertThrows(
                IllegalArgumentException.class,
                () -> new DefaultExecutionDecisionEngine(
                        0,
                        Duration.ofSeconds(timeout),
                        nowClock
                )
        );
    }
}