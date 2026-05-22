package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 인증 회원가입 요청")
public record CredentialSignUpRequest(
        @Schema(description = "사용자 닉네임", example = "lilamaris")
        String nickname,
        @Schema(description = "로그인에 사용할 이메일", example = "user@example.com")
        String email,
        @Schema(description = "로그인에 사용할 비밀번호", example = "password1234!")
        String password
) {
}
