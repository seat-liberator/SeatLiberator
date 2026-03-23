package com.seatliberator.seatliberator.idempotency.core.store;

import com.seatliberator.seatliberator.idempotency.core.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryIdempotencyStore")
public class InMemoryIdempotencyStoreTest {
    private Clock clock;
    private InMemoryIdempotencyStore store;

    @BeforeEach
    void setup() {
        clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        store = new InMemoryIdempotencyStore(clock);
    }

    @Test
    @DisplayName("없는 key로 getOrCreate를 호출하면 PENDING 상태의 신규 레코드를 생성한다")
    void 없는_key로_getOrCreate를_호출하면_PENDING_상태의_신규_레코드를_생성한다() {
        IdempotencyKey key = key("reservation-seat");
        IdempotencyContext context = context("user-1");

        var record = store.getOrCreate(key, context);

        assertAll(
                () -> assertTrue(record.newlyCreated()),
                () -> assertEquals(record.state().key(), key),
                () -> assertEquals(record.state().context(), context),
                () -> assertEquals(ExecutionPhase.PENDING, record.state().executionState().phase()),
                () -> assertEquals(record.state().createdAt(), clock.instant())
        );
    }

    @Test
    @DisplayName("같은 key로 다시 getOrCreate를 호출하면 기존 레코드를 반환한다")
    void 같은_key로_다시_getOrCreate를_호출하면_기존_레코드를_반환한다() {
        IdempotencyKey key = key("reservation-seat");
        IdempotencyContext firstContext = context("user-1");
        IdempotencyContext secondContext = context("user-2");

        var first = store.getOrCreate(key, firstContext);
        var second = store.getOrCreate(key, secondContext);

        assertAll(
                () -> assertTrue(first.newlyCreated()),
                () -> assertFalse(second.newlyCreated()),
                () -> assertEquals(second.state().context(), firstContext),
                () -> assertEquals(ExecutionPhase.PENDING, second.state().executionState().phase())
        );
    }

    @Test
    @DisplayName("PENDING 상태에서는 RUNNING 전이에 성공한다")
    void PENDING_상태에서는_RUNNING_전이에_성공한다() {
        IdempotencyKey key = key("reservation-seat");
        IdempotencyContext context = context("user-1");
        store.getOrCreate(key, context);

        boolean result = store.tryMarkRunning(key);
        assertTrue(result);

        var record = store.getOrCreate(key, context);
        assertAll(
                () -> assertEquals(ExecutionPhase.RUNNING, record.state().executionState().phase()),
                () -> assertEquals(1, record.state().executionState().attemptCount()),
                () -> assertEquals(clock.instant(), record.state().executionState().tryStartAt())
        );
    }

    @Test
    @DisplayName("존재하지 않는 key는 RUNNING 전이에 실패한다")
    void 존재하지_않는_key는_RUNNING_전이에_실패한다() {
        boolean result = store.tryMarkRunning(key("not-exists"));

        assertFalse(result);
    }

    @Test
    @DisplayName("이미 RUNNING 상태면 RUNNING 전이에 실패한다")
    void 이미_RUNNING_상태면_RUNNING_전이에_실패한다() {
        IdempotencyKey key = key("reservation-seat");
        IdempotencyContext context = context("user-1");
        store.getOrCreate(key, context);
        store.tryMarkRunning(key);

        boolean result = store.tryMarkRunning(key);

        assertFalse(result);
    }

    @Test
    @DisplayName("RUNNING 상태에서는 RESOLVED 전이에 성공한다")
    void RUNNING_상태에서는_RESOLVED_전이에_성공한다() {
        IdempotencyKey key = key("reserve-seat");
        store.getOrCreate(key, context("user-1"));
        store.tryMarkRunning(key);

        ExecutionOutput output = ImmutableExecutionOutput.success("OK");

        boolean result = store.tryMarkResolved(key, output);

        assertTrue(result);

        var record = store.getOrCreate(key, context("ignored"));
        assertAll(
                () -> assertEquals(ExecutionPhase.RESOLVED, record.state().executionState().phase()),
                () -> assertEquals(clock.instant(), record.state().executionState().resolvedAt()),
                () -> assertEquals(output, record.state().executionState().output())
        );
    }

    @Test
    @DisplayName("RUNNING 상태에서는 failure output으로도 RESOLVED 전이에 성공한다")
    void RUNNING_상태에서는_failure_output으로도_RESOLVED_전이에_성공한다() {
        IdempotencyKey key = key("reservation-seat");
        IdempotencyContext context = context("user-1");
        store.getOrCreate(key, context);
        store.tryMarkRunning(key);

        RuntimeException error = new RuntimeException("BOOOM");
        ExecutionOutput output = ImmutableExecutionOutput.failure(error);

        boolean result = store.tryMarkResolved(key, output);

        assertTrue(result);

        var record = store.getOrCreate(key, context);
        assertAll(
                () -> assertEquals(ExecutionPhase.RESOLVED, record.state().executionState().phase()),
                () -> assertEquals(output, record.state().executionState().output()),
                () -> assertEquals(clock.instant(), record.state().executionState().resolvedAt())
        );
    }

    @Test
    @DisplayName("RUNNING이 아닌 상태에서는 RESOLVED 전이에 실패한다")
    void RUNNING이_아닌_상태에서는_RESOLVED_전이에_실패한다() {
        IdempotencyKey key = key("reservation-seat");
        IdempotencyContext context = context("user-1");
        store.getOrCreate(key, context);

        ExecutionOutput output = ImmutableExecutionOutput.success("OK");
        boolean result = store.tryMarkResolved(key, output);

        assertFalse(result);
    }

    @Test
    @DisplayName("RUNNING 상태에서는 EXECUTION_ERROR 전이에 성공한다")
    void RUNNING_상태에서는_EXECUTION_ERROR_전이에_성공한다() {
        IdempotencyKey key = key("reserve-seat");
        IdempotencyContext context = context("user-1");
        store.getOrCreate(key, context);
        store.tryMarkRunning(key);

        boolean result = store.tryMarkExecutionError(key);

        assertTrue(result);

        var record = store.getOrCreate(key, context("ignored"));
        assertAll(
                () -> assertEquals(ExecutionPhase.EXECUTION_ERROR, record.state().executionState().phase()),
                () -> assertEquals(1, record.state().executionState().attemptCount())
        );
    }

    @Test
    @DisplayName("EXECUTION_ERROR 상태에서는 다시 RUNNING 전이에 성공하고 attemptCount가 증가한다")
    void EXECUTION_ERROR_상태에서는_다시_RUNNING_전이에_성공하고_attemptCount가_증가한다() {
        IdempotencyKey key = key("reserve-seat");
        IdempotencyContext context = context("user-1");
        store.getOrCreate(key, context);
        store.tryMarkRunning(key);
        store.tryMarkExecutionError(key);

        boolean result = store.tryMarkRunning(key);

        assertTrue(result);

        var record = store.getOrCreate(key, context("ignored"));

        assertAll(
                () -> assertEquals(ExecutionPhase.RUNNING, record.state().executionState().phase()),
                () -> assertEquals(2, record.state().executionState().attemptCount())
        );
    }

    @Test
    @DisplayName("동시에 여러 번 getOrCreate를 호출해도 최초 1회만 newlyCreated=true 이다")
    void getOrCreate_creates_only_once_under_race() throws Exception {
        IdempotencyKey key = key("reserve-seat");
        IdempotencyContext context = context("user-1");

        int threadCount = 16;
        var executor = Executors.newFixedThreadPool(threadCount);
        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);

        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                ready.countDown();
                start.await();
                return store.getOrCreate(key, context).newlyCreated();
            });
        }

        ready.await();
        start.countDown();

        List<Future<Boolean>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        long createdCount = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                createdCount++;
            }
        }

        assertEquals(1, createdCount);
    }

    @Test
    @DisplayName("동시에 여러 번 RUNNING 전이를 시도해도 정확히 한 번만 성공한다")
    void tryMarkRunning_allows_only_one_winner_under_race() throws Exception {
        IdempotencyKey key = key("reserve-seat");
        store.getOrCreate(key, context("user-1"));

        int threadCount = 16;
        var executor = Executors.newFixedThreadPool(threadCount);
        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);

        try {
            List<Future<Boolean>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    return store.tryMarkRunning(key);
                }));
            }

            ready.await();
            start.countDown();

            long successCount = 0;
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    successCount++;
                }
            }

            assertEquals(1, successCount);
        } finally {
            executor.shutdown();
        }
    }

    private IdempotencyKey key(String operation) {
        return new ImmutableIdempotencyKey("seat", operation);
    }

    private IdempotencyContext context(String userId) {
        return new ImmutableIdempotencyContext(String.valueOf(userId.hashCode()));
    }
}
