package com.seatliberator.seatliberator.identity.application.port.in.result;

import java.util.Set;
import java.util.UUID;

public record AuthEntry(
        UUID userId,
        String nickname,
        Set<String> scopes
) {
}
