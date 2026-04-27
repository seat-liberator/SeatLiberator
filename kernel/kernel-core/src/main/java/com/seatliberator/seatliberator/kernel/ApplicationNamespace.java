package com.seatliberator.seatliberator.kernel;

public interface ApplicationNamespace {
    String value();

    default boolean isSame(ApplicationNamespace namespace) {
        return value().equals(namespace.value());
    }
}
