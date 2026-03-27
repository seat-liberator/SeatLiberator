package com.seatliberator.seatliberator.identity.client.introspector.web;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

import java.util.Set;

public record WebActorResponse(
        String subject,
        Set<String> scopes
) implements Actor {
}
