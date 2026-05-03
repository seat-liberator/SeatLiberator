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

    public static Integer requireNegative(Integer value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value >= 0) throw new IllegalArgumentException(name + " must be negative.");
        return value;
    }

    public static Integer requireNonNegative(Integer value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value < 0) throw new IllegalArgumentException(name + " must be non-negative.");
        return value;
    }

    public static Integer requirePositive(Integer value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    public static Integer requireNonPositive(Integer value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value > 0) throw new IllegalArgumentException(name + " must be non-positive.");
        return value;
    }
}
