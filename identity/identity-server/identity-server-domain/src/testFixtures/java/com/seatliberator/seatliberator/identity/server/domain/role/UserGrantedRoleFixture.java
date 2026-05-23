package com.seatliberator.seatliberator.identity.server.domain.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;

import java.time.Instant;
import java.util.UUID;

public class UserGrantedRoleFixture {
    public static class Builder {
        private UUID userId;
        private NamespaceRole namespaceRole;
        private Instant createdAt;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder namespaceRole(NamespaceRole namespaceRole) {
            this.namespaceRole = namespaceRole;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserGrantedRole build() {
            return UserGrantedRole.of(userId, namespaceRole, createdAt);
        }
    }
}
