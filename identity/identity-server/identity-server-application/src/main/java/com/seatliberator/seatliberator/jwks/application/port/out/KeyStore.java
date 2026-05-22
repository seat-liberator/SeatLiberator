package com.seatliberator.seatliberator.jwks.application.port.out;

import com.seatliberator.seatliberator.identity.server.domain.jwks.RSASignatureKey;
import com.seatliberator.seatliberator.identity.server.domain.jwks.RSAVerificationKey;

import java.util.List;

public interface KeyStore {
    RSASignatureKey getSignableKey();

    List<RSAVerificationKey> getAllVerifiableKey();
}
