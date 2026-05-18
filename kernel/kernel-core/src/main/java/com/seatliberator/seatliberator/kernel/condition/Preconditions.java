package com.seatliberator.seatliberator.kernel.condition;

import java.time.Duration;
import java.util.Collection;
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

    public static <T extends Collection<?>> T requireNonEmpty(T value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value.isEmpty()) throw new IllegalArgumentException(name + " must not be empty.");
        return value;
    }

    public static <T extends Collection<?>> T requireNonEmptyElementsNonNull(T value, String name) {
        requireNonEmpty(value, name);
        if (value.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException(name + " must not contain null.");
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

    public static Integer requireAtLeast(Integer value, Integer minimum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(minimum, "minimum must not be null.");
        if (value < minimum) throw new IllegalArgumentException(name + " must be at least " + minimum + ".");
        return value;
    }

    public static Integer requireAtMost(Integer value, Integer maximum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(maximum, "maximum must not be null.");
        if (value > maximum) throw new IllegalArgumentException(name + " must be at most " + maximum + ".");
        return value;
    }

    public static Integer requireBetween(Integer value, Integer minimum, Integer maximum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(minimum, "minimum must not be null.");
        Objects.requireNonNull(maximum, "maximum must not be null.");
        if (minimum > maximum) throw new IllegalArgumentException("minimum must be less than or equal to maximum.");
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
        }
        return value;
    }

    public static Duration requireNegative(Duration value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (!value.isNegative()) throw new IllegalArgumentException(name + " must be negative.");
        return value;
    }

    public static Long requireNegative(Long value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value >= 0) throw new IllegalArgumentException(name + " must be negative.");
        return value;
    }

    public static Long requireNonNegative(Long value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value < 0) throw new IllegalArgumentException(name + " must be non-negative.");
        return value;
    }

    public static Long requirePositive(Long value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    public static Long requireNonPositive(Long value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value > 0) throw new IllegalArgumentException(name + " must be non-positive.");
        return value;
    }

    public static Long requireAtLeast(Long value, Long minimum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(minimum, "minimum must not be null.");
        if (value < minimum) throw new IllegalArgumentException(name + " must be at least " + minimum + ".");
        return value;
    }

    public static Long requireAtMost(Long value, Long maximum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(maximum, "maximum must not be null.");
        if (value > maximum) throw new IllegalArgumentException(name + " must be at most " + maximum + ".");
        return value;
    }

    public static Long requireBetween(Long value, Long minimum, Long maximum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(minimum, "minimum must not be null.");
        Objects.requireNonNull(maximum, "maximum must not be null.");
        if (minimum > maximum) throw new IllegalArgumentException("minimum must be less than or equal to maximum.");
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
        }
        return value;
    }

    public static Duration requireNonNegative(Duration value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value.isNegative()) throw new IllegalArgumentException(name + " must be non-negative.");
        return value;
    }

    public static Duration requirePositive(Duration value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (!value.isPositive()) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    public static Duration requireNonPositive(Duration value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        if (value.isPositive()) throw new IllegalArgumentException(name + " must be non-positive.");
        return value;
    }

    public static Duration requireAtLeast(Duration value, Duration minimum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(minimum, "minimum must not be null.");
        if (value.compareTo(minimum) < 0)
            throw new IllegalArgumentException(name + " must be at least " + minimum + ".");
        return value;
    }

    public static Duration requireAtMost(Duration value, Duration maximum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(maximum, "maximum must not be null.");
        if (value.compareTo(maximum) > 0)
            throw new IllegalArgumentException(name + " must be at most " + maximum + ".");
        return value;
    }

    public static Duration requireBetween(Duration value, Duration minimum, Duration maximum, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        Objects.requireNonNull(minimum, "minimum must not be null.");
        Objects.requireNonNull(maximum, "maximum must not be null.");
        if (minimum.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("minimum must be less than or equal to maximum.");
        }
        if (value.compareTo(minimum) < 0 || value.compareTo(maximum) > 0) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
        }
        return value;
    }
}
