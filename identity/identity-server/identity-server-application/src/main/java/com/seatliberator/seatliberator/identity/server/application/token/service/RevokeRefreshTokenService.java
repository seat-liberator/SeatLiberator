package com.seatliberator.seatliberator.identity.server.application.token.service;

import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.RevokeRefreshTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RevokeRefreshTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class RevokeRefreshTokenService implements RevokeRefreshTokenUseCase {
    private final RefreshTokenStore refreshTokenStore;
    private final Clock clock;

    @Override
    public void revoke(RevokeRefreshTokenCommand command) {
        var tokenHash = command.refreshToken();

        var refreshToken = refreshTokenStore.findActiveByTokenHash(tokenHash)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.AUTHENTICATION_FAILED));

        var now = clock.instant();
        refreshTokenStore.revoke(refreshToken, now);
    }
}
