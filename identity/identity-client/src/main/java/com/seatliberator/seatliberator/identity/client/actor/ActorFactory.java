package com.seatliberator.seatliberator.identity.client.actor;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

import java.util.Set;

public interface ActorFactory {
    Actor createActor(
            String subject,
            Set<String> scopes
    );
}
