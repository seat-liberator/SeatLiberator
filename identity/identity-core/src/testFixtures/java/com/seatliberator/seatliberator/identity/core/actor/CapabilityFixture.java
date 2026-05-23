package com.seatliberator.seatliberator.identity.core.actor;

public class CapabilityFixture {
    public static final String INITIAL_SCOPE = "test-scope";
    public static final String INITIAL_DESCRIPTION = "test scope description.";

    public static Capability createCapability() {
        return SimpleCapability.of(INITIAL_SCOPE, INITIAL_DESCRIPTION);
    }

    public static Capability createCapability(String scope) {
        return SimpleCapability.of(scope, INITIAL_DESCRIPTION);
    }

    public static Capability createCapability(String scope, String description) {
        return SimpleCapability.of(scope, description);
    }

    public static class Builder {
        private String scope = INITIAL_SCOPE;
        private String description = INITIAL_DESCRIPTION;

        public Builder() {}

        public Builder(String scope, String description) {
            this.scope = scope;
            this.description = description;
        }

        public static Builder from(Builder other) {
            return new Builder(other.scope, other.description);
        }

        public Builder copy() {
            return from(this);
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Capability build() {
            return SimpleCapability.of(scope, description);
        }
    }
}
