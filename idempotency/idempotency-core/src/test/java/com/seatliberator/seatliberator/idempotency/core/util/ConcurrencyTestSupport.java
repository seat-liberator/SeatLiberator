package com.seatliberator.seatliberator.idempotency.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyTestSupport<T> {
    private final Integer threadCount;
    private final WorkerFunction<T> workerFn;

    public ConcurrencyTestSupport(
            Integer threadCount,
            WorkerFunction<T> workerFn
    ) {
        this.threadCount = threadCount;
        this.workerFn = workerFn;
    }

    public WorkResult<T> run() throws InterruptedException {
        Map<Integer, T> successResult = new ConcurrentHashMap<>();
        Map<Integer, Throwable> failureResult = new ConcurrentHashMap<>();

        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threadCount);

        var success = new AtomicInteger();
        var failure = new AtomicInteger();

        var executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            var threadId = i;

            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();

                    T result = workerFn.execute();

                    successResult.put(threadId, result);
                    success.incrementAndGet();
                } catch (Exception e) {
                    failureResult.put(threadId, e);
                    failure.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(ready.await(3, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(5, TimeUnit.SECONDS)).isTrue();

        executor.shutdownNow();
        var successCount = success.get();
        var failureCount = failure.get();

        return new WorkResult<>(threadCount, successCount, failureCount, successResult, failureResult);
    }

    @FunctionalInterface
    public interface WorkerFunction<T> {
        T execute() throws Exception;
    }

    public record WorkResult<T>(
            Integer totalCount,
            Integer successCount,
            Integer failureCount,
            Map<Integer, T> successResult,
            Map<Integer, Throwable> failureResult
    ) {
    }
}
