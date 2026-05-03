package com.seatliberator.seatliberator.kernel.test;

import java.util.UUID;

public class UuidGenerator implements Generator<UUID> {
    private final Counter<Integer> counter;

    public UuidGenerator(Counter<Integer> counter) {
        if (counter == null) {
            throw new IllegalArgumentException("counter must not be null.");
        }
        this.counter = counter;
    }

    public static UUID generate(long value) {
        return new UUID(0L, value);
    }

    @Override
    public UUID generate() {
        return generate(counter.next());
    }
}
