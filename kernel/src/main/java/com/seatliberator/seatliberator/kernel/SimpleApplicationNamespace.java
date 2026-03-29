package com.seatliberator.seatliberator.kernel;

public record SimpleApplicationNamespace(String value) implements ApplicationNamespace {
    public SimpleApplicationNamespace {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("application namespace must not be blank.");
        }
    }

    public static SimpleApplicationNamespace from(String value) {
        return new SimpleApplicationNamespace(value);
    }
}
