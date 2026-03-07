package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

public class SimpleIntrospectionFactory implements IntrospectionFactory {

    @Override
    public Introspection createNoContent() {
        return new SimpleIntrospection(false, null, null);
    }

    @Override
    public Introspection createIntrospection(Long expiration, Actor actor) {
        return new SimpleIntrospection(true, expiration, actor);
    }
}
