package com.seatliberator.seatliberator.identity.server.security.shared.principal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.Set;
import java.util.UUID;

public record TrustedPrincipal(
        UUID userId,
        String nickname,
        Set<String> scopes
) {
    public TrustedPrincipal {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonBlank(nickname, "nickname");
        Preconditions.requireNonNull(scopes, "scopes");
    }

    public static TrustedPrincipal of(UUID userId, String nickname, Set<String> scopes) {
        return new TrustedPrincipal(userId, nickname, scopes);
    }
}
