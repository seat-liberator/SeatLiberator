package com.seatliberator.seatliberator.eventrelay.core.provider;

import org.jspecify.annotations.NonNull;

public class DefaultCorrelationIdProvider implements CorrelationIdProvider {
    private final IdGenerator generator;

    public DefaultCorrelationIdProvider(@NonNull IdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @NonNull String get() {
        return generator.generate();
    }
}
