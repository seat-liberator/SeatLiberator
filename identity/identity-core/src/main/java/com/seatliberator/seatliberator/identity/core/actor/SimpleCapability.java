package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record SimpleCapability(
        String scope,
        String description
) implements Capability {
    public SimpleCapability {
        Preconditions.requireNonBlank(scope, "scope");
        Preconditions.requireNonBlank(description, "description");
    }

    public static SimpleCapability of(String scope, String description) {
        return new SimpleCapability(scope, description);
    }

    public static SimpleCapability from(Capability capability) {
        Preconditions.requireNonNull(capability, "capability");

        return new SimpleCapability(capability.scope(), capability.description());
    }
}
