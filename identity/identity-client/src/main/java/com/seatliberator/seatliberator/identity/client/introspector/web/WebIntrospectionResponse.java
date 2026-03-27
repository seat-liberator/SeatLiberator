package com.seatliberator.seatliberator.identity.client.introspector.web;

import com.seatliberator.seatliberator.identity.core.introspection.Introspection;
import com.seatliberator.seatliberator.identity.core.introspection.SimpleIntrospection;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;

public record WebIntrospectionResponse(
        boolean active,
        Long expiration,
        WebActorResponse actor
) implements Introspection {
    public Introspection toDomain() {
        return new SimpleIntrospection(
                active,
                expiration,
                actor != null ? new SimpleActor(actor.subject(), actor.scopes()) : null
        );
    }
}
