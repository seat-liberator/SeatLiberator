package com.seatliberator.seatliberator.identity.server.redis.token.entry;

import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenRedisEntry(
        String tokenHash,
        String familyId,
        UUID userId,
        Instant issuedAt,
        Instant sessionIssuedAt,
        Instant idleExpiresAt,
        Instant sessionExpiresAt,
        Instant revokedAt
) {
    public static RefreshTokenRedisEntry from(RefreshToken refreshToken) {
        return new RefreshTokenRedisEntry(
                refreshToken.getTokenHash(),
                refreshToken.getFamilyId(),
                refreshToken.getUserId(),
                refreshToken.getIssuedAt(),
                refreshToken.getSessionIssuedAt(),
                refreshToken.getIdleExpiresAt(),
                refreshToken.getSessionExpiresAt(),
                refreshToken.getRevokedAt()
        );
    }

    public RefreshToken toDomain() {
        return RefreshToken.of(
                tokenHash,
                familyId,
                userId,
                issuedAt,
                sessionIssuedAt,
                idleExpiresAt,
                sessionExpiresAt,
                revokedAt
        );
    }
}