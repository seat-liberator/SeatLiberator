package com.seatliberator.seatliberator.eventrelay.core.provider;

import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface IdGenerator {
    @NonNull String generate();
}
