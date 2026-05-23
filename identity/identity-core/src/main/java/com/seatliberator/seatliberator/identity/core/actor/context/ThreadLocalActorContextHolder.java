package com.seatliberator.seatliberator.identity.core.actor.context;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.exception.UnidentifiableActorException;
import org.jspecify.annotations.NonNull;

public class ThreadLocalActorContextHolder implements ActorContextHolder {
    private static final ThreadLocal<Actor> CURRENT = new ThreadLocal<>();

    @Override
    public Actor getActor() {
        var actor = CURRENT.get();
        if (actor == null) {
            throw new UnidentifiableActorException("Does not held any actor");
        }
        return actor;
    }

    @Override
    public void setActor(@NonNull Actor actor) {
        CURRENT.set(actor);
    }

    @Override
    public void clear() {
        CURRENT.remove();
    }
}
