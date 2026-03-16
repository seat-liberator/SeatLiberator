package com.seatliberator.seatliberator.identity.client.validate.introspection.web;

import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.introspect.Introspection;
import com.seatliberator.seatliberator.identity.core.introspect.SimpleIntrospection;

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
