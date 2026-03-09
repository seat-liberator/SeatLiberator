package com.seatliberator.seatliberator.jwks.application.port.out;

import com.seatliberator.seatliberator.jwks.domain.RSASignatureKey;
import com.seatliberator.seatliberator.jwks.domain.RSAVerificationKey;

import java.util.List;

public interface KeyStore {
    RSASignatureKey getSignableKey();

    List<RSAVerificationKey> getAllVerifiableKey();
}
