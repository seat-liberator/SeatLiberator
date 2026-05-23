package com.seatliberator.seatliberator.identity.server.role;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.GrantRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.RevokeRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.UpdateRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.query.FindUserGrantedSummaryQuery;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.NamespaceRoleResult;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedSummaryResult;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.criteria.UserGrantedRoleUserNamespaceCriteria;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRoleFixture;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RoleUseCaseTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();

    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final UUID GRANTED_ROLE_ID = UuidGenerator.generate(2);
    public static final String NAMESPACE_VALUE = "reservation";
    public static final ApplicationNamespace NAMESPACE = SimpleApplicationNamespace.of(NAMESPACE_VALUE);
    public static final Role ROLE = Role.USER;
    public static final Role UPDATED_ROLE = Role.ADMIN;
    public static final SimpleNamespaceRole NAMESPACE_ROLE = SimpleNamespaceRole.from(NAMESPACE, ROLE);
    public static final Instant GRANTED_AT = CLOCK.instant();

    public static UserGrantedRole userGrantedRole() {
        var grantedRole = new UserGrantedRoleFixture.Builder()
                .userId(USER_ID)
                .namespaceRole(NAMESPACE_ROLE)
                .createdAt(GRANTED_AT)
                .build();
        stubId(grantedRole, GRANTED_ROLE_ID);
        return grantedRole;
    }

    public static GrantRoleCommand grantRoleCommand() {
        return GrantRoleCommand.of(USER_ID, NAMESPACE_ROLE);
    }

    public static UpdateRoleCommand updateRoleCommand() {
        return UpdateRoleCommand.of(USER_ID, NAMESPACE, UPDATED_ROLE);
    }

    public static RevokeRoleCommand revokeRoleCommand() {
        return RevokeRoleCommand.of(USER_ID, NAMESPACE);
    }

    public static FindUserGrantedSummaryQuery findUserGrantedSummaryQuery() {
        return FindUserGrantedSummaryQuery.of(USER_ID);
    }

    public static UserGrantedRoleUserNamespaceCriteria userGrantedRoleUserNamespaceCriteria() {
        return UserGrantedRoleUserNamespaceCriteria.of(USER_ID, NAMESPACE);
    }

    public static UserGrantedSummaryResult userGrantedSummaryResult() {
        return new UserGrantedSummaryResult(
                USER_ID,
                List.of(new NamespaceRoleResult(NAMESPACE_VALUE, ROLE))
        );
    }

    public static void stubId(UserGrantedRole grantedRole, UUID id) {
        try {
            var idField = UserGrantedRole.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(grantedRole, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("테스트용 ID 설정 실패");
        }
    }
}
