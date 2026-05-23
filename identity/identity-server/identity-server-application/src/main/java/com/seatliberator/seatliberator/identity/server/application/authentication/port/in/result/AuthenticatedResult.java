package com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedResult(
        UUID userId,
        String nickname,
        Set<String> scopes
) {
    public static AuthenticatedResult from(UUID userId, String nickname, Set<String> scopes) {
        return new AuthenticatedResult(userId, nickname, Set.copyOf(scopes));
    }
}
