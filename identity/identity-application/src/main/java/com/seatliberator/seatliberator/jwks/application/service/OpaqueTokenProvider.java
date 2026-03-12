package com.seatliberator.seatliberator.jwks.application.service;

import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import com.seatliberator.seatliberator.jwks.application.port.out.ByteEncoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.random.RandomGenerator;

@RequiredArgsConstructor
public class OpaqueTokenProvider implements TokenProvider {
    private final RandomGenerator randomGenerator;
    private final ByteEncoder byteEncoder;

    @Override
    public String issue(Map<String, Object> attributes) {
        byte[] randomBytes = new byte[32];
        randomGenerator.nextBytes(randomBytes);
        return byteEncoder.encode(randomBytes);
    }
}
