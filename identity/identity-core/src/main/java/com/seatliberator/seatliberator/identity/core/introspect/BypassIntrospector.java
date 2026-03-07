package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.ActorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

public class BypassIntrospector implements Introspector {
    private static final Logger log = LoggerFactory.getLogger(BypassIntrospector.class);

    private final ActorFactory actorFactory;
    private final IntrospectionFactory introspectionFactory;
    private final Long expiration;

    public BypassIntrospector(
            ActorFactory actorFactory,
            IntrospectionFactory introspectionFactory,
            Long expiration
    ) {
        this.actorFactory = actorFactory;
        this.introspectionFactory = introspectionFactory;
        this.expiration = expiration;
    }

    @Override
    public Introspection introspect(String token) {
        log.warn("""
                Introspect is not defined and is currently bypassed. this may cause security issues. Use this only when you need to exclude the module in test or development environment""");

        var fakeSubject = UUID.randomUUID().toString();
        var fakeActor = actorFactory.createActor(fakeSubject, Set.of());

        return introspectionFactory.createIntrospection(expiration, fakeActor);
    }
}
