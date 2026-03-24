package com.seatliberator.seatliberator.idempotency.core.integration;

import com.seatliberator.seatliberator.idempotency.core.exception.ExecutionContextMismatchException;
import com.seatliberator.seatliberator.idempotency.core.processor.IdempotentProcessor;
import com.seatliberator.seatliberator.idempotency.core.util.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.atomic.AtomicInteger;

import static com.seatliberator.seatliberator.idempotency.core.util.IdempotencyTestUtil.context;
import static com.seatliberator.seatliberator.idempotency.core.util.IdempotencyTestUtil.key;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(IdempotentProcessorTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("DefaultIdempotencyProcessorIntegration - Concurrency")
public class DefaultIdempotentProcessorIntegrationConcurrencyTest {

    private final int threadCount = 50;
    @Autowired
    private IdempotentProcessor processor;

    @Test
    @DisplayName("동시에 process 호출해도 action은 한 번만 실행되고 모든 호출은 resolved 결과를 반환한다")
    void 동시에_process_호출해도_action은_한_번만_실행되고_모든_호출은_resolved_결과를_반환한다() throws Exception {
        var key = key("seat", "reserve");
        var ctx = context("user-1");

        var executionCount = new AtomicInteger();

        var support = new ConcurrencyTestSupport<>(threadCount, () -> processor.process(key, ctx, () -> {
            executionCount.incrementAndGet();
            Thread.sleep(30);
            return "OK";
        }));

        var result = support.run();

        assertThat(executionCount.get()).isOne();
        assertThat(result.successCount()).isEqualTo(threadCount);
        assertThat(result.failureCount()).isZero();
        assertThat(result.successResult().values()).allMatch("OK"::equals);
    }

    @Test
    @DisplayName("동시에 process 호출해도 action 실패는 한 번만 실행되고 모든 호출은 같은 예외를 재사용한다")
    void 동시에_process_호출해도_action_실패는_한번만_실행되고_모든_호출은_같은_예외를_재사용한다() throws Exception {
        var key = key("seat", "reserve");
        var ctx = context("user-1");

        var executionCount = new AtomicInteger();

        var support = new ConcurrencyTestSupport<>(threadCount, () -> processor.process(key, ctx, () -> {
            executionCount.incrementAndGet();
            Thread.sleep(30);
            throw new IllegalArgumentException("boom");
        }));

        var result = support.run();

        assertThat(executionCount.get()).isOne();
        assertThat(result.successCount()).isZero();
        assertThat(result.failureCount()).isEqualTo(threadCount);
        assertThat(result.failureResult().values())
                .allSatisfy(error -> {
                    assertThat(error).isInstanceOf(IllegalArgumentException.class);
                });
    }

    @Test
    @DisplayName("이미 resolved success인 경우 action을 다시 실행하지 않고 기존 결과를 반환한다")
    void 이미_resolved_success인_경우_action을_다시_실행하지_않고_기존_결과를_반환한다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");

        var executionCount = new AtomicInteger();

        String initial = processor.process(key, ctx, () -> {
            executionCount.incrementAndGet();
            return "OK";
        });

        String second = processor.process(key, ctx, () -> {
            executionCount.incrementAndGet();
            return "SHOULD_NOT_RUN";
        });

        assertThat(initial).isEqualTo("OK");
        assertThat(second).isEqualTo("OK");
        assertThat(executionCount.get()).isOne();
    }

    @Test
    @DisplayName("이미 resolved failure인 경우 action을 다시 실행하지 않고 기존 예외를 반환한다")
    void 이미_resolved_failure인_경우_action을_다시_실행하지_않고_기존_예외를_반환한다() {
        var key = key("seat", "reserve");
        var ctx = context("user-1");

        var executionCount = new AtomicInteger();

        assertThatThrownBy(() -> processor.process(key, ctx, () -> {
            executionCount.incrementAndGet();
            throw new IllegalArgumentException("boom");
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("boom");

        assertThatThrownBy(() -> processor.process(key, ctx, () -> {
            executionCount.incrementAndGet();
            throw new IllegalArgumentException("should not run");
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("boom");

        assertThat(executionCount.get()).isOne();
    }

    @Test
    @DisplayName("같은 key에 다른 context로 호출하면 context mismatch로 reject 된다")
    void 같은_key에_다른_context로_호출하면_context_mismatch로_reject_된다() {
        var key = key("seat", "reserve");
        var ctx1 = context("user-1");
        var ctx2 = context("user-2");

        var executionCount = new AtomicInteger();

        String first = processor.process(key, ctx1, () -> {
            executionCount.incrementAndGet();
            return "OK";
        });

        assertThat(first).isEqualTo("OK");

        assertThatThrownBy(() -> processor.process(key, ctx2, () -> {
            executionCount.incrementAndGet();
            return "SHOULD_NOT_RUN";
        }))
                .isInstanceOf(ExecutionContextMismatchException.class);

        assertThat(executionCount.get()).isOne();
    }
}

