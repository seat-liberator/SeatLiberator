package com.seatliberator.seatliberator.identity.server.application.token.internal;

@FunctionalInterface
public interface ByteEncoder {
    String encode(byte[] bytes);
}
