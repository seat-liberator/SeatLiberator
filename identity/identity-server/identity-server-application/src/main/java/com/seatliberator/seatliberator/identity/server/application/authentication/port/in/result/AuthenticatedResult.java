package com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedResult(
        UUID userId,
        String nickname,
        Set<String> grantedRole
) {
    public static AuthenticatedResult from(UUID userId, String nickname, Set<String> grantedRole) {
        return new AuthenticatedResult(userId, nickname, Set.copyOf(grantedRole));
    }
}
