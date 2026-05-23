package com.seatliberator.seatliberator.identity.core.actor.context;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.NonNull;

public interface ActorContextHolder {
    Actor getActor();

    void setActor(@NonNull Actor actor);

    void clear();
}
