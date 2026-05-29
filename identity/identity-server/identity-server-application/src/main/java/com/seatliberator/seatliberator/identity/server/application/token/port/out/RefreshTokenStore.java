package com.seatliberator.seatliberator.identity.server.application.token.port.out;

import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenStore {
    boolean existsRevokedByTokenHash(String tokenHash);

    Optional<RefreshToken> findActiveByTokenHash(String tokenHash);

    void save(RefreshToken refreshToken, Instant now);

    void revoke(RefreshToken refreshToken, Instant revokedAt);
}