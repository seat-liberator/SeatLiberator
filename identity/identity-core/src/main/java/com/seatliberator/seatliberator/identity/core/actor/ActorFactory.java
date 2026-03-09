package com.seatliberator.seatliberator.identity.core.actor;

import java.util.Set;

public interface ActorFactory {
    Actor createActor(
            String subject,
            Set<String> scopes
    );
}
