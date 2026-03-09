package com.seatliberator.seatliberator.jwks.application.port.in;

import com.seatliberator.seatliberator.jwks.domain.RSASignatureKey;
import com.seatliberator.seatliberator.jwks.domain.RSAVerificationKey;

import java.util.List;

public interface KeyProvider {
    List<RSAVerificationKey> getVerificationKeys();

    RSASignatureKey getSignatureKey();
}
