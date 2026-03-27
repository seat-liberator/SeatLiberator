package com.seatliberator.seatliberator.identity.client.introspector;

public interface Introspector {
    com.seatliberator.seatliberator.identity.core.introspection.Introspection introspect(String token);
}
