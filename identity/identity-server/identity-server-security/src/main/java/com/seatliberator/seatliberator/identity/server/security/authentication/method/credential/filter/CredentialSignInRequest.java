package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 인증 로그인 요청")
public record CredentialSignInRequest(
        @Schema(description = "로그인 이메일", example = "user@example.com")
        String email,
        @Schema(description = "로그인 비밀번호", example = "password1234!")
        String password
) {
}
