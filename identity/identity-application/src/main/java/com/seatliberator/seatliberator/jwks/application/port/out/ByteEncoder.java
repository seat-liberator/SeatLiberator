package com.seatliberator.seatliberator.jwks.application.port.out;

@FunctionalInterface
public interface ByteEncoder {
    String encode(byte[] bytes);
}
