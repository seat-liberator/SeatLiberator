package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.Nullable;

public interface Introspection {
    boolean active();

    @Nullable Long expiration();

    @Nullable Actor actor();
}
