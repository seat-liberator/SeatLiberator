package com.seatliberator.seatliberator.identity.server.redis.token.entry;

import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public record RevokedRefreshTokenRedisEntry(
        String tokenHash,
        String familyId,
        UUID userId,
        Instant revokedAt
) {
    public static RevokedRefreshTokenRedisEntry from(
            RefreshToken refreshToken,
            Instant revokedAt
    ) {
        return new RevokedRefreshTokenRedisEntry(
                refreshToken.getTokenHash(),
                refreshToken.getFamilyId(),
                refreshToken.getUserId(),
                revokedAt
        );
    }
}
