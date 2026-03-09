package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.exception.UnidentifiableActorException;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;

public class ThreadLocalActorContextHolder implements ActorContextHolder {
    private static final ThreadLocal<Actor> CURRENT = new ThreadLocal<>();
    private static final String MDC_KEY = "actor_context";

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
        MDC.put(MDC_KEY, actor.subject());
    }

    @Override
    public void clear() {
        CURRENT.remove();
        MDC.remove(MDC_KEY);
    }
}
