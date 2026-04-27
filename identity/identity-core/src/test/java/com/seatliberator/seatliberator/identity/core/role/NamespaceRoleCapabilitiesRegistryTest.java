package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Namespace Role Capabilities Registry")
class NamespaceRoleCapabilitiesRegistryTest {

    @Test
    @DisplayName("상위 Role은 하위 Role의 capability를 함께 가진다")
    void higher_role_inherits_lower_role_capabilities() {
        var namespace = SimpleApplicationNamespace.of("reservation");
        var guestCapability = new SimpleCapability("room.list", "방 목록 조회");
        var userCapability = new SimpleCapability("booking.create", "예약 생성");
        var maintainerCapability = new SimpleCapability("room.manage", "방 관리");

        var registry = new NamespaceRoleCapabilitiesRegistry(namespace, List.of(
                new RoleCapabilities(Role.GUEST, Set.of(guestCapability)),
                new RoleCapabilities(Role.USER, Set.of(userCapability)),
                new RoleCapabilities(Role.MAINTAINER, Set.of(maintainerCapability)),
                new RoleCapabilities(Role.ADMIN, Set.of())
        ));

        assertThat(registry.resolve(namespace, Role.GUEST))
                .containsExactlyInAnyOrder(guestCapability);
        assertThat(registry.resolve(namespace, Role.USER))
                .containsExactlyInAnyOrder(guestCapability, userCapability);
        assertThat(registry.resolve(namespace, Role.MAINTAINER))
                .containsExactlyInAnyOrder(guestCapability, userCapability, maintainerCapability);
        assertThat(registry.resolve(namespace, Role.ADMIN))
                .containsExactlyInAnyOrder(guestCapability, userCapability, maintainerCapability);
    }

    @Test
    @DisplayName("NamespaceRole로 resolve할 때도 누적 capability를 반환한다")
    void resolve_by_namespace_role_returns_inherited_capabilities() {
        var namespace = SimpleApplicationNamespace.of("reservation");
        var guestCapability = new SimpleCapability("room.list", "방 목록 조회");
        var userCapability = new SimpleCapability("room.read", "방 조회");
        var userRole = SimpleNamespaceRole.from(namespace, Role.USER);

        var registry = new NamespaceRoleCapabilitiesRegistry(namespace, List.of(
                new RoleCapabilities(Role.GUEST, Set.of(guestCapability)),
                new RoleCapabilities(Role.USER, Set.of(userCapability))
        ));

        assertThat(registry.resolve(userRole))
                .containsExactlyInAnyOrder(guestCapability, userCapability);
    }

    @Test
    @DisplayName("동일 Role capability가 중복 등록되면 예외")
    void throw_exception_when_role_capabilities_is_duplicated() {
        var namespace = SimpleApplicationNamespace.of("reservation");

        assertThatThrownBy(() -> new NamespaceRoleCapabilitiesRegistry(namespace, List.of(
                new RoleCapabilities(Role.USER, Set.of()),
                new RoleCapabilities(Role.USER, Set.of())
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Duplicated role capabilities. role=USER");
    }
}
