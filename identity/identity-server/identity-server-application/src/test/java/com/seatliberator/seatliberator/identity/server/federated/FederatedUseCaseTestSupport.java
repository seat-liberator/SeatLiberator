package com.seatliberator.seatliberator.identity.server.federated;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.LinkFederatedAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.RegisterFederatedAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.UnlinkFederatedAccountCommand;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccountFixture;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.identity.server.domain.account.UserFixture;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRoleFixture;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FederatedUseCaseTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();

    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String NICKNAME = "nickname";
    public static final Instant USER_CREATED_AT = CLOCK.instant();

    public static final String REGISTRATION_ID = "google";
    public static final String PROVIDER_USER_ID = "google-user-1";
    public static final String PROVIDER_USER_NICKNAME = "google-nickname";

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

    public static RegisterFederatedAccountCommand registerFederatedAccountCommand() {
        return RegisterFederatedAccountCommand.of(REGISTRATION_ID, PROVIDER_USER_ID, PROVIDER_USER_NICKNAME);
    }

    public static LinkFederatedAccountCommand linkFederatedAccountCommand() {
        return LinkFederatedAccountCommand.of(USER_ID, REGISTRATION_ID, PROVIDER_USER_ID);
    }

    public static UnlinkFederatedAccountCommand unlinkFederatedAccountCommand() {
        return UnlinkFederatedAccountCommand.of(USER_ID, REGISTRATION_ID);
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
