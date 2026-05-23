package com.seatliberator.seatliberator.identity.server.domain.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.time.Instant;
import java.util.UUID;

import static com.seatliberator.seatliberator.identity.server.domain.role.RoleTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserGrantedRole 테스트")
public class UserGrantedRoleTest {
    private UserGrantedRole createUserGrantedRole() {
        return new UserGrantedRoleFixture.Builder()
                .userId(USER_ID)
                .namespaceRole(NAMESPACE_ROLE)
                .createdAt(ROLE_GRANTED_AT)
                .build();
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("userId, namespaceRole, createdAt으로 생성한다")
        void create_with_user_id_namespace_role_and_created_at() {
            var userGrantedRole = UserGrantedRole.of(USER_ID, NAMESPACE_ROLE, ROLE_GRANTED_AT);

            assertThat(userGrantedRole.getUserId()).isEqualTo(USER_ID);
            assertThat(userGrantedRole.getNamespaceRole()).isInstanceOf(EmbeddableNamespaceRole.class);
            assertThat(userGrantedRole.getNamespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
            assertThat(userGrantedRole.getNamespaceRole().role()).isEqualTo(ROLE);
            assertThat(userGrantedRole.getCreatedAt()).isEqualTo(ROLE_GRANTED_AT);
        }

        @Test
        @DisplayName("fixture로 UserGrantedRole을 생성한다")
        void create_with_fixture() {
            var userGrantedRole = createUserGrantedRole();

            assertThat(userGrantedRole.getUserId()).isEqualTo(USER_GRANTED_ROLE.getUserId());
            assertThat(userGrantedRole.getNamespaceRole().namespace().value())
                    .isEqualTo(USER_GRANTED_ROLE.getNamespaceRole().namespace().value());
            assertThat(userGrantedRole.getNamespaceRole().role()).isEqualTo(USER_GRANTED_ROLE.getNamespaceRole().role());
            assertThat(userGrantedRole.getCreatedAt()).isEqualTo(USER_GRANTED_ROLE.getCreatedAt());
        }

        @ParameterizedTest(name = "userId = {0}")
        @NullSource
        @DisplayName("userId가 null이면 예외")
        void throw_exception_when_null_user_id(UUID userId) {
            assertThatDomainThrownBy(() -> UserGrantedRole.of(userId, NAMESPACE_ROLE, ROLE_GRANTED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("userId");
        }

        @ParameterizedTest(name = "namespaceRole = {0}")
        @NullSource
        @DisplayName("namespaceRole이 null이면 예외")
        void throw_exception_when_null_namespace_role(NamespaceRole namespaceRole) {
            assertThatDomainThrownBy(() -> UserGrantedRole.of(USER_ID, namespaceRole, ROLE_GRANTED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("namespaceRole");
        }

        @ParameterizedTest(name = "createdAt = {0}")
        @NullSource
        @DisplayName("createdAt이 null이면 예외")
        void throw_exception_when_null_created_at(Instant createdAt) {
            assertThatDomainThrownBy(() -> UserGrantedRole.of(USER_ID, NAMESPACE_ROLE, createdAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("createdAt");
        }
    }

    @Nested
    @DisplayName("role 변경 테스트")
    class UpdateRoleTest {
        @Test
        @DisplayName("role을 변경한다")
        void update_role() {
            var userGrantedRole = createUserGrantedRole();

            userGrantedRole.updateRole(UPDATED_ROLE);

            assertThat(userGrantedRole.getNamespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
            assertThat(userGrantedRole.getNamespaceRole().role()).isEqualTo(UPDATED_ROLE);
            assertThat(userGrantedRole.getCreatedAt()).isEqualTo(ROLE_GRANTED_AT);
        }

        @ParameterizedTest(name = "role = {0}")
        @NullSource
        @DisplayName("변경할 role이 null이면 예외")
        void throw_exception_when_update_role_is_null(Role role) {
            var userGrantedRole = createUserGrantedRole();

            assertThatDomainThrownBy(() -> userGrantedRole.updateRole(role))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("role");
        }
    }
}
