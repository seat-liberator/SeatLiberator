package com.seatliberator.seatliberator.jwks.domain;

import lombok.Getter;

import java.security.interfaces.RSAPublicKey;

@Getter
public class RSAVerificationKey extends AbstractKeyMetadata {
    private final RSAPublicKey rsaPublicKey;

    public RSAVerificationKey(
            String kid,
            KeyStatus status,
            RSAPublicKey rsaPublicKey
    ) {
        super(kid, status);
        this.rsaPublicKey = rsaPublicKey;
    }
}
