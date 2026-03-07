package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

public interface IntrospectionFactory {
    Introspection createNoContent();

    Introspection createIntrospection(Long expiration, Actor actor);
}
