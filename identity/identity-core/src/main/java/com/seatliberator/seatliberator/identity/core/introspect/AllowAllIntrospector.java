package com.seatliberator.seatliberator.identity.core.introspect;

import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

public class AllowAllIntrospector implements Introspector {
    private static final Logger log = LoggerFactory.getLogger(AllowAllIntrospector.class);

    private final IntrospectionFactory introspectionFactory;
    private final Long expiration;

    public AllowAllIntrospector(
            IntrospectionFactory introspectionFactory,
            Long expiration
    ) {
        this.introspectionFactory = introspectionFactory;
        this.expiration = expiration;
    }

    @Override
    public Introspection introspect(String token) {
        log.warn("AllowAllIntrospector is enabled. Actual token introspection is bypassed, and every token is considered active. expirationMs={}", expiration);

        var fakeSubject = UUID.randomUUID().toString();
        var fakeActor = SimpleActor.of(fakeSubject, Set.of());

        return introspectionFactory.createIntrospection(expiration, fakeActor);
    }
}
