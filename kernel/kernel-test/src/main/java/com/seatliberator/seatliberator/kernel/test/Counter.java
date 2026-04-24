package com.seatliberator.seatliberator.kernel.test;

public interface Counter<T> {
    T next();

    Class<T> support();
}
