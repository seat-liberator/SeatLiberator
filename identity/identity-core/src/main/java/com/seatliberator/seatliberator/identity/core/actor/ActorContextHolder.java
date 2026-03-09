package com.seatliberator.seatliberator.identity.core.actor;

import org.jspecify.annotations.NonNull;

public interface ActorContextHolder {
    Actor getActor();

    void setActor(@NonNull Actor actor);

    void clear();
}
