package com.seatliberator.seatliberator.identity.server.security.authentication.method.token;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 성공 후 발급된 토큰")
public record IssuedTokenEntry(
        @Schema(description = "API 인증에 사용할 액세스 토큰")
        String accessToken,
        @Schema(description = "액세스 토큰 재발급에 사용할 리프레시 토큰")
        String refreshToken
) {
}
