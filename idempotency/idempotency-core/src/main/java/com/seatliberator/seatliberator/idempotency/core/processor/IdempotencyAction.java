package com.seatliberator.seatliberator.idempotency.core.processor;

@FunctionalInterface
public interface IdempotencyAction<T> {
    T execute() throws Exception;
}
