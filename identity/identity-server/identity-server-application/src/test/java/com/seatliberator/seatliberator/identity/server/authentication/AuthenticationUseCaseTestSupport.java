package com.seatliberator.seatliberator.identity.server.authentication;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationCredentialCommand;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationFederatedCommand;
import com.seatliberator.seatliberator.identity.server.domain.account.*;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRoleFixture;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AuthenticationUseCaseTestSupport {
    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String NICKNAME = "nickname";
    public static final Instant USER_CREATED_AT = TestClock.getFixed().instant();

    public static final String EMAIL = "test-user@example.com";
    public static final String PASSWORD = "password";
    public static final String WRONG_PASSWORD = "wrong-password";
    public static final String PASSWORD_HASH = "{bcrypt}password-hash";

    public static final String REGISTRATION_ID = "google";
    public static final String PROVIDER_USER_ID = "google-user-1";

    public static final String NAMESPACE_VALUE = "identity";
    public static final Role ROLE = Role.USER;
    public static final SimpleNamespaceRole NAMESPACE_ROLE = SimpleNamespaceRole.from(
            SimpleApplicationNamespace.of(NAMESPACE_VALUE),
            ROLE
    );
    public static final String SCOPE = "identity:USER";
    public static final Set<String> SCOPES = Set.of(SCOPE);

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

    public static FederatedAccount federatedAccount() {
        return new FederatedAccountFixture.Builder()
                .userId(USER_ID)
                .registrationId(REGISTRATION_ID)
                .providerUserId(PROVIDER_USER_ID)
                .createdAt(USER_CREATED_AT)
                .build();
    }

    public static UserGrantedRole userGrantedRole() {
        return new UserGrantedRoleFixture.Builder()
                .userId(USER_ID)
                .namespaceRole(NAMESPACE_ROLE)
                .createdAt(USER_CREATED_AT)
                .build();
    }

    public static List<UserGrantedRole> userGrantedRoles() {
        return List.of(userGrantedRole());
    }

    public static AuthenticationCredentialCommand authenticationCredentialCommand() {
        return AuthenticationCredentialCommand.of(EMAIL, PASSWORD);
    }

    public static AuthenticationFederatedCommand authenticationFederatedCommand() {
        return AuthenticationFederatedCommand.of(REGISTRATION_ID, PROVIDER_USER_ID);
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
