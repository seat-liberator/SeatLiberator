package com.seatliberator.seatliberator.identity.server.domain.token;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class RefreshToken {
    private final String tokenHash;
    private final String familyId;
    private final UUID userId;

    private final Instant issuedAt;
    private final Instant sessionIssuedAt;

    private final Instant idleExpiresAt;
    private final Instant sessionExpiresAt;
    private final Instant revokedAt;

    private RefreshToken(
            String tokenHash,
            String familyId,
            UUID userId,
            Instant issuedAt,
            Instant sessionIssuedAt,
            Instant idleExpiresAt,
            Instant sessionExpiresAt,
            Instant revokedAt
    ) {
        this.tokenHash = Preconditions.requireNonBlank(tokenHash, "tokenHash");
        this.familyId = Preconditions.requireNonBlank(familyId, "familyId");
        this.userId = Preconditions.requireNonNull(userId, "userId");
        this.issuedAt = Preconditions.requireNonNull(issuedAt, "issuedAt");
        this.sessionIssuedAt = Preconditions.requireNonNull(sessionIssuedAt, "sessionIssuedAt");
        this.idleExpiresAt = Preconditions.requireNonNull(idleExpiresAt, "idleExpiresAt");
        this.sessionExpiresAt = Preconditions.requireNonNull(sessionExpiresAt, "sessionExpiresAt");
        this.revokedAt = revokedAt;

        if (issuedAt.isBefore(sessionIssuedAt)) {
            throw new IllegalArgumentException("issuedAt must not be before sessionIssuedAt.");
        }

        if (idleExpiresAt.isAfter(sessionExpiresAt)) {
            throw new IllegalArgumentException("idleExpiresAt must not be after sessionExpiresAt.");
        }

        if (!idleExpiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException("idleExpiresAt must be after issuedAt.");
        }

        if (!sessionExpiresAt.isAfter(sessionIssuedAt)) {
            throw new IllegalArgumentException("sessionExpiresAt must be after sessionIssuedAt.");
        }

        if (revokedAt != null && revokedAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("revokedAt must not be before issuedAt.");
        }
    }

    public static RefreshToken issue(String tokenHash, String familyId, UUID userId, Instant issuedAt, Duration idleTtl, Duration sessionTtl) {
        Preconditions.requireNonNull(issuedAt, "issuedAt");
        Preconditions.requirePositive(idleTtl, "idleTtl");
        Preconditions.requirePositive(sessionTtl, "sessionTtl");

        var sessionExpiresAt = issuedAt.plus(sessionTtl);
        var idleExpiresAt = min(issuedAt.plus(idleTtl), sessionExpiresAt);
        return new RefreshToken(tokenHash, familyId, userId, issuedAt, issuedAt, idleExpiresAt, sessionExpiresAt, null);
    }

    public static RefreshToken of(
            String tokenHash,
            String familyId,
            UUID userId,
            Instant issuedAt,
            Instant sessionIssuedAt,
            Instant idleExpiresAt,
            Instant sessionExpiresAt,
            Instant revokedAt
    ) {
        return new RefreshToken(
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

    public RefreshToken renew(String tokenHash, Instant renewedAt, Duration idleTtl) {
        Preconditions.requireNonNull(renewedAt, "renewedAt");
        Preconditions.requirePositive(idleTtl, "idleTtl");

        if (isExpiredAt(renewedAt))
            throw new IllegalArgumentException("Expired refresh token cannot be renewed.");

        var idleExpiresAt = min(renewedAt.plus(idleTtl), sessionExpiresAt);
        return new RefreshToken(tokenHash, familyId, userId, renewedAt, sessionIssuedAt, idleExpiresAt, sessionExpiresAt, revokedAt);
    }

    public RefreshToken revoke(Instant revokedAt) {
        Preconditions.requireNonNull(revokedAt, "revokedAt");

        return new RefreshToken(tokenHash, familyId, userId, issuedAt, sessionIssuedAt, idleExpiresAt, sessionExpiresAt, revokedAt);
    }

    public boolean isExpiredAt(Instant at) {
        Preconditions.requireNonNull(at, "at");

        return !at.isBefore(effectiveExpiresAt());
    }

    public Duration remainingTtlAt(Instant at) {
        Preconditions.requireNonNull(at, "at");

        if (isExpiredAt(at)) {
            return Duration.ZERO;
        }

        return Duration.between(at, effectiveExpiresAt());
    }

    private static Instant min(Instant a, Instant b) {
        return a.isBefore(b) ? a : b;
    }

    private Instant effectiveExpiresAt() {
        if (revokedAt == null) {
            return idleExpiresAt;
        }

        return min(idleExpiresAt, revokedAt);
    }
}
