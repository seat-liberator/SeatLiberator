package com.seatliberator.seatliberator.identity.server.token;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.CreateAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RefreshAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RevokeRefreshTokenCommand;
import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Clock;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class TokenUseCaseTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();

    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN_HASH = "refresh-token-hash";
    public static final String RENEWED_REFRESH_TOKEN_HASH = "renewed-refresh-token-hash";
    public static final String FAMILY_ID = "refresh-token-family-id";

    public static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(5);
    public static final Duration REFRESH_TOKEN_IDLE_TTL = Duration.ofMinutes(30);
    public static final Duration REFRESH_TOKEN_SESSION_TTL = Duration.ofDays(14);

    public static Jwt accessToken() {
        var issuedAt = CLOCK.instant();
        return new Jwt(
                ACCESS_TOKEN,
                issuedAt,
                issuedAt.plus(ACCESS_TOKEN_TTL),
                Map.of("alg", "none"),
                Map.of("sub", USER_ID.toString())
        );
    }

    public static RefreshToken refreshToken() {
        return RefreshToken.issue(
                REFRESH_TOKEN_HASH,
                FAMILY_ID,
                USER_ID,
                CLOCK.instant(),
                REFRESH_TOKEN_IDLE_TTL,
                REFRESH_TOKEN_SESSION_TTL
        );
    }

    public static RefreshToken renewedRefreshToken() {
        return refreshToken().renew(RENEWED_REFRESH_TOKEN_HASH, CLOCK.instant(), REFRESH_TOKEN_IDLE_TTL);
    }

    public static CreateAccessTokenCommand createAccessTokenCommand() {
        return CreateAccessTokenCommand.of(USER_ID);
    }

    public static RefreshAccessTokenCommand refreshAccessTokenCommand() {
        return RefreshAccessTokenCommand.of(REFRESH_TOKEN_HASH);
    }

    public static RevokeRefreshTokenCommand revokeRefreshTokenCommand() {
        return RevokeRefreshTokenCommand.of(REFRESH_TOKEN_HASH);
    }
}
