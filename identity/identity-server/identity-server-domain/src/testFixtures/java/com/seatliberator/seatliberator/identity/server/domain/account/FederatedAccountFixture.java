package com.seatliberator.seatliberator.identity.server.domain.account;

import java.time.Instant;
import java.util.UUID;

public class FederatedAccountFixture {
    public static class Builder {
        private UUID userId;
        private String registrationId;
        private String providerUserId;
        private Instant createdAt;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder registrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        public Builder providerUserId(String providerUserId) {
            this.providerUserId = providerUserId;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FederatedAccount build() {
            return FederatedAccount.of(userId, registrationId, providerUserId, createdAt);
        }
    }
}
