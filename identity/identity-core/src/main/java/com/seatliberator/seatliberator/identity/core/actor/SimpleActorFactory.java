package com.seatliberator.seatliberator.identity.core.actor;

import java.util.Set;

public class SimpleActorFactory implements ActorFactory {
    @Override
    public Actor createActor(String subject, Set<String> scopes) {
        return new SimpleActor(subject, scopes);
    }
}
