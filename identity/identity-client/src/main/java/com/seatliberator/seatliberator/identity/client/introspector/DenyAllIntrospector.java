package com.seatliberator.seatliberator.identity.client.introspector;

import com.seatliberator.seatliberator.identity.core.introspection.IntrospectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DenyAllIntrospector implements Introspector {
    private static final Logger log = LoggerFactory.getLogger(DenyAllIntrospector.class);
    private final IntrospectionFactory introspectionFactory;

    public DenyAllIntrospector(
            IntrospectionFactory introspectionFactory
    ) {
        this.introspectionFactory = introspectionFactory;
    }

    @Override
    public com.seatliberator.seatliberator.identity.core.introspection.Introspection introspect(String token) {
        log.warn("DenyAllIntrospector is enabled. Actual token introspection is bypassed, and every token is considered inactive.");
        return introspectionFactory.createNoContent();
    }
}
