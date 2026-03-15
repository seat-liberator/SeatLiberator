package com.seatliberator.seatliberator.identity.client.validate.introspection.web;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

import java.util.Set;

public record WebActorResponse(
        String subject,
        Set<String> scopes
) implements Actor {
}
