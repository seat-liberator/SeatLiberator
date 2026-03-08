package com.seatliberator.seatliberator.jwks.application.port.in;

import com.seatliberator.seatliberator.jwks.domain.SigningKey;

import java.util.List;

public record KeyProviderResult(
        List<SigningKey> keys
) {
}
