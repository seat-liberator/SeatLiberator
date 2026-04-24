package com.seatliberator.seatliberator.kernel;

public record FixedCurrentApplicationNamespaceProvider(
        ApplicationNamespace current
) implements CurrentApplicationNamespaceProvider {
    public FixedCurrentApplicationNamespaceProvider {
        if (current == null) {
            throw new IllegalArgumentException("current application namespace must not be null.");
        }
    }

    public FixedCurrentApplicationNamespaceProvider(String namespace) {
        this(SimpleApplicationNamespace.of(namespace));
    }
}
