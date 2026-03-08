package com.seatliberator.seatliberator.jwks.application.service;

import com.seatliberator.seatliberator.jwks.application.port.in.KeyProvider;
import com.seatliberator.seatliberator.jwks.application.port.in.KeyProviderResult;
import com.seatliberator.seatliberator.jwks.application.port.out.KeyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JWKSProvider implements KeyProvider {
    private final KeyStore keyStore;

    @Override
    public KeyProviderResult getVerifiableKeys() {
        var keys = keyStore.getAllVerifiableKey();

        return new KeyProviderResult(keys);
    }
}
