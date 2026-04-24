package com.seatliberator.seatliberator.kernel.test;

@FunctionalInterface
public interface Generator<T> {
    T generate();
}
