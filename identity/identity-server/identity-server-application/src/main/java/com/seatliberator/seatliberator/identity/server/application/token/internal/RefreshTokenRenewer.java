package com.seatliberator.seatliberator.identity.server.application.token.internal;

import com.seatliberator.seatliberator.identity.server.application.token.config.TokenProperties;
import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshTokenRenewer {
    private final OpaqueTokenGenerator tokenCreator;
    private final TokenProperties properties;

    public RefreshToken renewRefreshToken(RefreshToken exists, Instant renewedAt) {
        var refreshTokenProperties = properties.refreshToken();
        var tokenHash = tokenCreator.create();
        var idleTtl = refreshTokenProperties.idleTtl();

        return exists.renew(tokenHash, renewedAt, idleTtl);
    }
}
