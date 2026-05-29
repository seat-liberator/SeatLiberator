package com.seatliberator.seatliberator.identity.server.application.token.internal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class Hasher {
    private final String algorithm;
    private final SecretKeySpec keySpec;

    public Hasher(String algorithm, String secret) {
        Preconditions.requireNonBlank(secret, "secret");
        this.algorithm = Preconditions.requireNonBlank(algorithm, "algorithm");
        this.keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm);
    }

    public byte[] hash(byte[] plainBytes) {
        try {
            var mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac.doFinal(plainBytes);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to hash.", e);
        }
    }
}
