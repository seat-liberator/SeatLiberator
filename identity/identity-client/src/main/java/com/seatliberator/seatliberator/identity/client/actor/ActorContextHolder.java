package com.seatliberator.seatliberator.identity.client.actor;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.NonNull;

public interface ActorContextHolder {
    Actor getActor();

    void setActor(@NonNull Actor actor);

    void clear();
}
