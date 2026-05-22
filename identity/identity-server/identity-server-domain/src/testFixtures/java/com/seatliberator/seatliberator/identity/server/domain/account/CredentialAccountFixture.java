package com.seatliberator.seatliberator.identity.server.domain.account;

import java.time.Instant;
import java.util.UUID;

public class CredentialAccountFixture {
    public static class Builder {
        private UUID userId;
        private String email;
        private String passwordHash;
        private Instant createdAt;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CredentialAccount build() {
            return CredentialAccount.of(userId, email, passwordHash, createdAt);
        }
    }
}
