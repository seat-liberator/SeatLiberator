package com.seatliberator.seatliberator.identity.server.application.account.port.in.result;

import java.util.Set;
import java.util.UUID;

public record AuthEntry(
        UUID userId,
        String nickname,
        Set<String> scopes
) {
}
