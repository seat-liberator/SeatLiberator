package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.request;

public record CredentialSignUpRequest(
        String nickname,
        String email,
        String password
) {
}
