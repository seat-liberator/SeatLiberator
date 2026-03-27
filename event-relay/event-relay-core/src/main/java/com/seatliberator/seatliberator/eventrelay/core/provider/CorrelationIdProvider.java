package com.seatliberator.seatliberator.eventrelay.core.provider;

import org.jspecify.annotations.NonNull;

public interface CorrelationIdProvider {
    @NonNull String get();
}
