package com.seatliberator.seatliberator.identity.server.application.token.internal;

import com.seatliberator.seatliberator.identity.server.application.jwks.port.out.ByteEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

@Component
@RequiredArgsConstructor
public class OpaqueTokenGenerator {
    private final RandomGenerator randomGenerator;
    private final Hasher hasher;
    private final ByteEncoder byteEncoder;

    public String create() {
        byte[] randomBytes = new byte[32];
        randomGenerator.nextBytes(randomBytes);

        var byteHash = hasher.hash(randomBytes);
        return byteEncoder.encode(byteHash);
    }
}
