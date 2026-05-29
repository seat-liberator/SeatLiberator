package com.seatliberator.seatliberator.identity.server.application.token.port.out;

import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;

import java.time.Instant;

public interface RefreshTokenRotator {
    RefreshTokenRotationResult rotate(
            RefreshToken oldToken,
            RefreshToken newToken,
            Instant rotatedAt
    );
}
