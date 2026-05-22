package com.seatliberator.seatliberator.identity.server.domain.jwks;

import lombok.Getter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
public class RSASignatureKey extends RSAVerificationKey {
    private final RSAPrivateKey rsaPrivateKey;

    public RSASignatureKey(
            String kid,
            KeyStatus status,
            RSAPublicKey rsaPublicKey,
            RSAPrivateKey rsaPrivateKey
    ) {
        super(kid, status, rsaPublicKey);
        this.rsaPrivateKey = rsaPrivateKey;
    }
}