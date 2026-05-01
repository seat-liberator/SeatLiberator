package com.seatliberator.seatliberator.kernel.condition;

import java.util.Objects;

public class Preconditions {
    public static <T> T requireNonNull(T value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        return value;
    }

    public static String requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value.isBlank()) throw new IllegalArgumentException(name + " must not be blank.");
        return value;
    }
}
