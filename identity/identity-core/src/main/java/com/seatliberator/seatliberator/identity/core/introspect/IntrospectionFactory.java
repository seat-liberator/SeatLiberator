package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.NonNull;

public interface IntrospectionFactory {
    Introspection createNoContent();

    Introspection createIntrospection(@NonNull Long expiration, @NonNull Actor actor);
}
