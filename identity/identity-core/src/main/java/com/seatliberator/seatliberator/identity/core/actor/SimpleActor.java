package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.role.Capability;

import java.util.Set;

public record SimpleActor(
        String subject,
        Set<Capability> capabilities
) implements Actor {
    public SimpleActor {
        if (subject == null || subject.isBlank())
            throw new IllegalArgumentException("subject must not be null or blank.");
        if (capabilities == null) throw new IllegalArgumentException("capabilities must not be null.");
        capabilities = Set.copyOf(capabilities);
    }

    public static SimpleActor of(String subject, Set<? extends Capability> capabilities) {
        return new SimpleActor(subject, copyCapabilities(capabilities));
    }

    public static SimpleActor from(Actor actor) {
        if (actor == null) throw new IllegalArgumentException("actor must not be null.");
        return new SimpleActor(actor.subject(), copyCapabilities(actor.capabilities()));
    }

    private static Set<Capability> copyCapabilities(Set<? extends Capability> capabilities) {
        if (capabilities == null) throw new IllegalArgumentException("capabilities must not be null.");
        return Set.copyOf(capabilities);
    }
}
