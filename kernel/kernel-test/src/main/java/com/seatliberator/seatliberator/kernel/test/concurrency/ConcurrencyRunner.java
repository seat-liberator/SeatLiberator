package com.seatliberator.seatliberator.kernel.test.concurrency;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ConcurrencyRunner {
    private final Properties properties;

    public ConcurrencyRunner(Properties properties) {
        this.properties = Preconditions.requireNonNull(properties, "properties");
    }

    public <T> RunnerResult<T> run(Function<Integer, T> fn) throws InterruptedException {
        var threadCount = properties.threadCount();
        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threadCount);

        ConcurrentHashMap<Integer, T> success = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, Exception> failure = new ConcurrentHashMap<>();

        var executor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                var threadId = i;

                executor.submit(() -> {
                    ready.countDown();

                    try {
                        start.await();
                        var result = fn.apply(threadId);
                        success.put(threadId, result);
                    } catch (Exception e) {
                        failure.put(threadId, e);
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await(3, TimeUnit.SECONDS);
            start.countDown();
            done.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        return new RunnerResult<>(success, failure);
    }

    public record Properties(int threadCount) {
    }

    public record RunnerResult<T>(
            ConcurrentHashMap<Integer, T> success,
            ConcurrentHashMap<Integer, Exception> failure
    ) {
    }
}
