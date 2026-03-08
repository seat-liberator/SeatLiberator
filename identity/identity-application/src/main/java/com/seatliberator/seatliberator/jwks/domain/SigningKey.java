package com.seatliberator.seatliberator.jwks.domain;

import com.nimbusds.jose.jwk.RSAKey;

public record SigningKey(
        String kid,
        KeyStatus status,
        RSAKey rsaKey
) {
    public boolean canSign() {
        return status == KeyStatus.SIGNABLE;
    }

    public boolean canVerify() {
        return status == KeyStatus.VERIFY_ONLY;
    }

    public RSAKey toPublicJwk() {
        return rsaKey.toPublicJWK();
    }
}
