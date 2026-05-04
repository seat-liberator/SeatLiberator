package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.role.Capability;
import com.seatliberator.seatliberator.identity.core.role.CapabilityFixture;

import java.util.Set;

public class ActorFixture {
    public static final String INITIAL_SUBJECT = "user-subject-1";
    public static final Set<Capability> INITIAL_CAPABILITIES = Set.of(
            CapabilityFixture.createCapability("capability-1"),
            CapabilityFixture.createCapability("capability-2"),
            CapabilityFixture.createCapability("capability-3")
    );

    public static Actor get() {
        return new SimpleActor(INITIAL_SUBJECT, INITIAL_CAPABILITIES);
    }

    public static class Builder {
        private String subject = INITIAL_SUBJECT;
        private Set<Capability> capabilities = INITIAL_CAPABILITIES;

        public Builder() {}

        public Builder(String subject, Set<Capability> capabilities) {
            this.subject = subject;
            this.capabilities = capabilities;
        }

        public static Builder from(Builder other) {
            return new Builder(other.subject, Set.copyOf(other.capabilities));
        }

        public Builder copy() {
            return from(this);
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder capabilities(Set<Capability> capabilities) {
            this.capabilities = Set.copyOf(capabilities);
            return this;
        }

        public Actor build() {
            return SimpleActor.of(subject, capabilities);
        }
    }
}
