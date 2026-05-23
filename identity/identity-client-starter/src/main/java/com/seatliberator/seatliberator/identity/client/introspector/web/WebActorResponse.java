package com.seatliberator.seatliberator.identity.client.introspector.web;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleCapability;

import java.util.Set;

public record WebActorResponse(
        String subject,
        Set<SimpleCapability> capabilities
) implements Actor {
}
