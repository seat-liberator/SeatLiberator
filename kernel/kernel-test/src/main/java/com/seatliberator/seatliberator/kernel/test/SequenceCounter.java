package com.seatliberator.seatliberator.kernel.test;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceCounter implements Counter<Integer> {
    private final AtomicInteger counter;

    public SequenceCounter() {
        this(0);
    }

    public SequenceCounter(int initialValue) {
        this.counter = new AtomicInteger(initialValue);
    }

    @Override
    public Integer next() {
        return counter.getAndIncrement();
    }

    @Override
    public Class<Integer> support() {
        return Integer.class;
    }
}
