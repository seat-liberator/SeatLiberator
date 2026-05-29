package com.seatliberator.seatliberator.identity.server.application.token.port.in.result;

public record AccessTokenResult(
        String accessToken,
        String refreshToken
) {
    public static AccessTokenResult of(String accessToken, String refreshToken) {
        return new AccessTokenResult(accessToken, refreshToken);
    }
}
