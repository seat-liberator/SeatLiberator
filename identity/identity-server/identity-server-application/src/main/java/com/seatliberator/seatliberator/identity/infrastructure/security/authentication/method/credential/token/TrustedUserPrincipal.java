package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token;

import java.util.Set;

public record TrustedUserPrincipal(
        String subject,
        String nickname,
        Set<String> scopes
) {
}
