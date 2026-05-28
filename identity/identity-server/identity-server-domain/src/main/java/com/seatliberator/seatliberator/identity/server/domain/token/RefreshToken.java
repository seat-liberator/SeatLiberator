package com.seatliberator.seatliberator.identity.server.domain.token;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Getter
public class RefreshToken {
    private final String tokenHash;

    private final String subject;

    private final Instant issuedAt;

    private final Instant expiresAt;

    private RefreshToken(String tokenHash, String subject, Instant issuedAt, Instant expiresAt) {
        this.tokenHash = Preconditions.requireNonBlank(tokenHash, "tokenHash");
        this.subject = Preconditions.requireNonBlank(subject, "subject");
        this.issuedAt = Preconditions.requireNonNull(issuedAt, "issuedAt");
        this.expiresAt = Preconditions.requireNonNull(expiresAt, "expiresAt");
    }

    public static RefreshToken issue(String tokenHash, String subject, Instant issuedAt, Duration ttl) {
        return new RefreshToken(tokenHash, subject, issuedAt, issuedAt.plus(ttl));
    }

    public static RefreshToken of(String tokenHash, String subject, Instant issuedAt, Instant expiresAt) {
        return new RefreshToken(tokenHash, subject, issuedAt, expiresAt);
    }

    public boolean isExpiresAt(Instant at) {
        Preconditions.requireNonNull(at, "at");

        return !at.isBefore(expiresAt);
    }

    public Duration remainingTtlAt(Instant at) {
        Preconditions.requireNonNull(at, "at");

        if (isExpiresAt(at)) {
            return Duration.ZERO;
        }

        return Duration.between(at, expiresAt);
    }
}
