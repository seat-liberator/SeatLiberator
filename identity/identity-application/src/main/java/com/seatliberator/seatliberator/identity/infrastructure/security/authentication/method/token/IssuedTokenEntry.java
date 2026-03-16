package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token;

public record IssuedTokenEntry(
        String accessToken,
        String refreshToken
) {
}
