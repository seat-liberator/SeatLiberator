package com.seatliberator.seatliberator.kernel;

public record SimpleApplicationNamespace(String value) implements ApplicationNamespace {
    public SimpleApplicationNamespace {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("application namespace must not be blank.");
        }
    }

    public static SimpleApplicationNamespace from(ApplicationNamespace namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("application namespace must not be null.");
        }
        return new SimpleApplicationNamespace(namespace.value());
    }

    public static SimpleApplicationNamespace of(String value) {
        return new SimpleApplicationNamespace(value);
    }
}
