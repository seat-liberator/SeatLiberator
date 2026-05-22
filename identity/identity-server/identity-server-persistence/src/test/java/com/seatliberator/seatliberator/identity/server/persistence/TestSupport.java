package com.seatliberator.seatliberator.identity.server.persistence;

import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class TestSupport {
    public static final Clock CLOCK = TestClock.getFixed();

    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final UUID OTHER_USER_ID = UuidGenerator.generate(2);

    public static final String NICKNAME = "nickname";
    public static final String EMAIL = "test-user@example.com";
    public static final String OTHER_EMAIL = "other-user@example.com";
    public static final String PASSWORD_HASH = "{bcrypt}password-hash";
    public static final String REGISTRATION_ID = "google";
    public static final String OTHER_REGISTRATION_ID = "github";
    public static final String PROVIDER_USER_ID = "google-user-1";
    public static final String OTHER_PROVIDER_USER_ID = "google-user-2";

    public static final Instant CREATED_AT = CLOCK.instant();

    private TestSupport() {
    }

    public static User user() {
        return User.of(NICKNAME, CREATED_AT);
    }

    public static CredentialAccount credentialAccount() {
        return CredentialAccount.of(USER_ID, EMAIL, PASSWORD_HASH, CREATED_AT);
    }

    public static FederatedAccount federatedAccount(UUID userId) {
        return FederatedAccount.of(userId, REGISTRATION_ID, PROVIDER_USER_ID, CREATED_AT);
    }
}
