package com.seatliberator.seatliberator.eventrelay.core.provider;

import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.jspecify.annotations.NonNull;

public class ApplicationNamespaceProducerProvider implements ProducerProvider {
    private final CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider;

    public ApplicationNamespaceProducerProvider(
            CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider
    ) {
        this.currentApplicationNamespaceProvider = currentApplicationNamespaceProvider;
    }

    @Override
    public @NonNull String get() {
        return currentApplicationNamespaceProvider.current().value();
    }
}
