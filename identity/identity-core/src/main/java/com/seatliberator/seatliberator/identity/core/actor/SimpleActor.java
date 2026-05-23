package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.Set;

public record SimpleActor(
        String subject,
        Set<Capability> capabilities
) implements Actor {
    public SimpleActor {
        Preconditions.requireNonBlank(subject, "subject");
        capabilities = Set.copyOf(Preconditions.requireNonNull(capabilities, "capabilities"));
    }

    public static SimpleActor of(String subject, Set<Capability> capabilities) {
        return new SimpleActor(subject, capabilities);
    }

    public static SimpleActor from(Actor actor) {
        Preconditions.requireNonNull(actor, "actor");

        return new SimpleActor(actor.subject(), actor.capabilities());
    }
}
