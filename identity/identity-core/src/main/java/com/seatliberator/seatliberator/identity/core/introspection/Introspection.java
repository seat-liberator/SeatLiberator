package com.seatliberator.seatliberator.identity.core.introspection;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.Nullable;

public interface Introspection {
    boolean active();

    @Nullable Long expiration();

    @Nullable Actor actor();
}
