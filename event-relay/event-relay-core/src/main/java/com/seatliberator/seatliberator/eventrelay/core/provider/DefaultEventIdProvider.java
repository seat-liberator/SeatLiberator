package com.seatliberator.seatliberator.eventrelay.core.provider;

import org.jspecify.annotations.NonNull;

public class DefaultEventIdProvider implements EventIdProvider {
    private final IdGenerator generator;

    public DefaultEventIdProvider(@NonNull IdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @NonNull String get() {
        return generator.generate();
    }
}
