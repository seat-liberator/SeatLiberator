package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.request;

public record CredentialSignInRequest(
        String email,
        String password
) {
}
