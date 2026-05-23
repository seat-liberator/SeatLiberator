package com.seatliberator.seatliberator.identity.server.domain.role;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Instant;
import java.util.UUID;

public class RoleTestSupport {
    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String NAMESPACE_VALUE = "reservation";
    public static final ApplicationNamespace NAMESPACE = SimpleApplicationNamespace.of(NAMESPACE_VALUE);
    public static final Role ROLE = Role.USER;
    public static final Role UPDATED_ROLE = Role.ADMIN;
    public static final SimpleNamespaceRole NAMESPACE_ROLE = SimpleNamespaceRole.from(NAMESPACE, ROLE);
    public static final Instant ROLE_GRANTED_AT = TestClock.getFixed().instant();
    public static final UserGrantedRole USER_GRANTED_ROLE = new UserGrantedRoleFixture.Builder()
            .userId(USER_ID)
            .namespaceRole(NAMESPACE_ROLE)
            .createdAt(ROLE_GRANTED_AT)
            .build();
}
