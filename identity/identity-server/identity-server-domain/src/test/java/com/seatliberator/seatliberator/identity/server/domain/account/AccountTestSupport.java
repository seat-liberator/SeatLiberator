package com.seatliberator.seatliberator.identity.server.domain.account;

import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Instant;
import java.util.UUID;

public class AccountTestSupport {
    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String USER_NICKNAME = "user-nickname";
    public static final String UPDATED_USER_NICKNAME = "updated-user-nickname";
    public static final Instant USER_CREATED_AT = TestClock.getFixed().instant();
    public static final Instant USER_UPDATED_AT = USER_CREATED_AT.plusSeconds(1);
    public static final User USER = new UserFixture.Builder()
            .nickname(USER_NICKNAME)
            .createdAt(USER_CREATED_AT)
            .build();

    public static final String EMAIL = "test-user@example.com";
    public static final String PASSWORD_HASH = "{bcrypt}password-hash";
    public static final String UPDATED_PASSWORD_HASH = "{bcrypt}updated-password-hash";

    public static final String REGISTRATION_ID = "google";
    public static final String PROVIDER_USER_ID = "google-1";
}
