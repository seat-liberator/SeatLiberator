package com.seatliberator.seatliberator.identity.server.application.jwks.port.out;

@FunctionalInterface
public interface ByteEncoder {
    String encode(byte[] bytes);
}
