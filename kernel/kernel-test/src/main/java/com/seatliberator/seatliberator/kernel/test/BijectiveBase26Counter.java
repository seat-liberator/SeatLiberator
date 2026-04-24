package com.seatliberator.seatliberator.kernel.test;

import java.util.concurrent.atomic.AtomicInteger;

public class BijectiveBase26Counter implements Counter<String> {
    private final AtomicInteger counter;

    public BijectiveBase26Counter() {
        this(0);
    }

    public BijectiveBase26Counter(int initialValue) {
        if (initialValue < 0) {
            throw new IllegalArgumentException("initialValue must not be negative.");
        }

        this.counter = new AtomicInteger(initialValue);
    }

    @Override
    public String next() {
        var sb = new StringBuilder();
        var n = counter.getAndIncrement();
        while (n >= 0) {
            sb.append((char) ('A' + (n % 26)));
            n = (n / 26) - 1;
        }
        return sb.reverse().toString();
    }

    @Override
    public Class<String> support() {
        return String.class;
    }
}
