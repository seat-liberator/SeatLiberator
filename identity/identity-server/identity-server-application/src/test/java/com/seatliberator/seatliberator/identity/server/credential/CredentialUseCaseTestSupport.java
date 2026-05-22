package com.seatliberator.seatliberator.identity.server.credential;

import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.RegisterCredentialAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.UpdatePasswordCommand;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccountFixture;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.identity.server.domain.account.UserFixture;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class CredentialUseCaseTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();

    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String NICKNAME = "nickname";
    public static final Instant USER_CREATED_AT = CLOCK.instant();

    public static final String EMAIL = "test-user@example.com";
    public static final String PASSWORD = "password";
    public static final String OLD_PASSWORD = "old-password";
    public static final String NEW_PASSWORD = "new-password";
    public static final String PASSWORD_HASH = "{bcrypt}password-hash";
    public static final String ENCODED_PASSWORD_HASH = "{bcrypt}encoded-password-hash";

    public static final Set<String> SCOPES = Set.of("identity:user");

    public static User user() {
        var user = new UserFixture.Builder()
                .nickname(NICKNAME)
                .createdAt(USER_CREATED_AT)
                .build();
        stubId(user, USER_ID);
        return user;
    }

    public static CredentialAccount credentialAccount() {
        return new CredentialAccountFixture.Builder()
                .userId(USER_ID)
                .email(EMAIL)
                .passwordHash(PASSWORD_HASH)
                .createdAt(USER_CREATED_AT)
                .build();
    }

    public static RegisterCredentialAccountCommand registerCredentialAccountCommand() {
        return RegisterCredentialAccountCommand.of(NICKNAME, EMAIL, PASSWORD);
    }

    public static UpdatePasswordCommand updatePasswordCommand() {
        return UpdatePasswordCommand.of(USER_ID, OLD_PASSWORD, NEW_PASSWORD);
    }

    public static void stubId(User user, UUID id) {
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("테스트용 ID 설정 실패");
        }
    }
}
