package com.seatliberator.seatliberator.identity.server.domain.account;

import java.time.Instant;

public class UserFixture {
    public static class Builder {
        private String nickname;
        private Instant createdAt;

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            return User.of(nickname, createdAt);
        }
    }
}
