package com.seatliberator.seatliberator.identity.client.actor;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;

import java.util.Set;

public class SimpleActorFactory implements ActorFactory {
    @Override
    public Actor createActor(String subject, Set<String> scopes) {
        return new SimpleActor(subject, scopes);
    }
}
