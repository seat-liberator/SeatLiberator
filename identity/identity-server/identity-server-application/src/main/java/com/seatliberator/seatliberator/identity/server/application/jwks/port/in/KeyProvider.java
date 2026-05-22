package com.seatliberator.seatliberator.identity.server.application.jwks.port.in;

import com.seatliberator.seatliberator.identity.server.domain.jwks.RSASignatureKey;
import com.seatliberator.seatliberator.identity.server.domain.jwks.RSAVerificationKey;

import java.util.List;

public interface KeyProvider {
    List<RSAVerificationKey> getVerificationKeys();

    RSASignatureKey getSignatureKey();
}
