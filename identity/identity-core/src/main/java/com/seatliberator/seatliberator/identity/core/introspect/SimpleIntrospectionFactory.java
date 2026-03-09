package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class SimpleIntrospectionFactory implements IntrospectionFactory {

    @Override
    public Introspection createNoContent() {
        return new SimpleIntrospection(false, null, null);
    }

    @Override
    public Introspection createIntrospection(@NonNull Long expiration, @NonNull Actor actor) {
        Objects.requireNonNull(expiration);
        Objects.requireNonNull(actor);
        return new SimpleIntrospection(true, expiration, actor);
    }
}
