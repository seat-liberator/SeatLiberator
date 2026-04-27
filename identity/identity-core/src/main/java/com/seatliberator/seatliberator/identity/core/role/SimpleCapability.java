package com.seatliberator.seatliberator.identity.core.role;

public record SimpleCapability(
        String scope,
        String description
) implements Capability {
    public SimpleCapability {
        if (scope == null || scope.isBlank()) throw new IllegalArgumentException("scope must not be null or blank.");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("description must not be null or blank.");
    }

    public static SimpleCapability of(String scope, String description) {
        return new SimpleCapability(scope, description);
    }

    public static SimpleCapability from(Capability capability) {
        if (capability == null) throw new IllegalArgumentException("capability must not be null.");
        return new SimpleCapability(capability.scope(), capability.description());
    }
}
