package com.seatliberator.seatliberator.jwks.application.service;

import com.seatliberator.seatliberator.jwks.application.port.in.KeyProvider;
import com.seatliberator.seatliberator.jwks.application.port.out.KeyStore;
import com.seatliberator.seatliberator.jwks.domain.RSASignatureKey;
import com.seatliberator.seatliberator.jwks.domain.RSAVerificationKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RSAKeyProvider implements KeyProvider {
    private final KeyStore keyStore;

    @Override
    public List<RSAVerificationKey> getVerificationKeys() {
        return keyStore.getAllVerifiableKey();
    }

    @Override
    public RSASignatureKey getSignatureKey() {
        return keyStore.getSignableKey();
    }
}
