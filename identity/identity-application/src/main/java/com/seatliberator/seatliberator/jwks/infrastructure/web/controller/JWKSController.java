package com.seatliberator.seatliberator.jwks.infrastructure.web.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.seatliberator.seatliberator.jwks.application.port.in.KeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JWKSController {
    private final KeyProvider keyProvider;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        var result = keyProvider.getVerificationKeys();

        var keys = result.stream()
                .map(rsaVerificationKey ->
                        new RSAKey.Builder(rsaVerificationKey.getRsaPublicKey())
                                .keyID(rsaVerificationKey.getKid())
                                .keyUse(KeyUse.SIGNATURE)
                                .algorithm(JWSAlgorithm.RS256)
                                .build()
                )
                .map(RSAKey::toJSONObject)
                .toList();

        return Map.of("keys", keys);
    }
}
