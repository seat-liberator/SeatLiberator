package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token;

public record TrustedUserPrincipal(
        String subject,
        String nickname
) {
}
