package com.seatliberator.seatliberator.identity.server.application.token.service;

import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.token.internal.AccessTokenIssuer;
import com.seatliberator.seatliberator.identity.server.application.token.internal.RefreshTokenRenewer;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.RefreshAccessTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RefreshAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.result.AccessTokenResult;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenRotationResult;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenRotator;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {
    private final RefreshTokenStore refreshTokenStore;
    private final RefreshTokenRotator refreshTokenRotator;

    private final UserReader userReader;
    private final AccessTokenIssuer accessTokenIssuer;
    private final RefreshTokenRenewer refreshTokenRenewer;
    private final Clock clock;

    @Override
    public AccessTokenResult refresh(RefreshAccessTokenCommand command) {
        var tokenHash = command.refreshToken();
        var exists = refreshTokenStore.findActiveByTokenHash(tokenHash)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.AUTHENTICATION_FAILED));

        var userId = exists.getUserId();

        var now = clock.instant();
        var renewed = refreshTokenRenewer.renewRefreshToken(exists, now);

        exists.revoke(now);

        var renewedResult = refreshTokenRotator.rotate(
                exists,
                renewed,
                now
        );

        if (renewedResult != RefreshTokenRotationResult.SUCCESS)
            throw new IdentityApplicationException(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        var accessToken = accessTokenIssuer.issue(userId, now);

        return AccessTokenResult.of(
                accessToken.getTokenValue(),
                renewed.getTokenHash()
        );
    }
}
