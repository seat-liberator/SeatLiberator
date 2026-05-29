package com.seatliberator.seatliberator.identity.server.application.token.internal;

import com.seatliberator.seatliberator.identity.server.application.token.config.TokenProperties;
import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenFactory {
    private final OpaqueTokenGenerator tokenCreator;
    private final TokenProperties properties;

    public RefreshToken create(String familyId, UUID userId, Instant issuedAt) {
        var refreshTokenProperties = properties.refreshToken();
        var tokenHash = tokenCreator.create();
        var idleTtl = refreshTokenProperties.idleTtl();
        var sessionTtl = refreshTokenProperties.sessionTtl();

        return RefreshToken.issue(tokenHash, familyId, userId, issuedAt, idleTtl, sessionTtl);
    }
}
