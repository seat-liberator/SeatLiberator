package com.seatliberator.seatliberator.jwks.infrastructure.web.controller;

import com.nimbusds.jose.jwk.RSAKey;
import com.seatliberator.seatliberator.jwks.application.port.in.KeyProvider;
import com.seatliberator.seatliberator.jwks.domain.SigningKey;
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
        var result = keyProvider.getVerifiableKeys();

        var keys = result.keys().stream()
                .map(SigningKey::toPublicJwk)
                .map(RSAKey::toJSONObject)
                .toList();

        return Map.of("keys", keys);
    }
}
