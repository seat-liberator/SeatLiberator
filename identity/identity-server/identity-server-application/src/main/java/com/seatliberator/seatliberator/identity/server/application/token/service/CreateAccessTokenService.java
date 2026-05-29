package com.seatliberator.seatliberator.identity.server.application.token.service;

import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.token.internal.AccessTokenIssuer;
import com.seatliberator.seatliberator.identity.server.application.token.internal.RefreshTokenFactory;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.CreateAccessTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.CreateAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.result.AccessTokenResult;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAccessTokenService implements CreateAccessTokenUseCase {
    private final RefreshTokenStore refreshTokenStore;
    private final AccessTokenIssuer accessTokenIssuer;

    private final UserReader userReader;
    private final UserGrantedRoleReader grantedRoleReader;
    private final RefreshTokenFactory refreshTokenFactory;
    private final Clock clock;

    @Override
    public AccessTokenResult create(CreateAccessTokenCommand command) {
        var userId = command.userId();
        var now = clock.instant();

        var accessToken = accessTokenIssuer.issue(userId, now);

        var familyId = UUID.randomUUID().toString();
        var refreshToken = refreshTokenFactory.create(familyId, userId, now);
        refreshTokenStore.save(refreshToken, now);

        return AccessTokenResult.of(
                accessToken.getTokenValue(),
                refreshToken.getTokenHash()
        );
    }
}
