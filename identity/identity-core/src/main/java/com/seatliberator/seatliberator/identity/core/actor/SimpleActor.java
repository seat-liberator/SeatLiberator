package com.seatliberator.seatliberator.identity.core.actor;

import java.util.Set;

public record SimpleActor(
        String subject,
        Set<String> scopes
) implements Actor {
}
