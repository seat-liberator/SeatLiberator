package com.seatliberator.seatliberator.identity.server.domain.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.seatliberator.seatliberator.identity.server.domain.role.RoleTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmbeddableNamespaceRole 테스트")
public class EmbeddableNamespaceRoleTest {
    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("namespace와 role로 생성한다")
        void create_with_namespace_and_role() {
            var namespaceRole = EmbeddableNamespaceRole.of(NAMESPACE, ROLE);

            assertThat(namespaceRole).isInstanceOf(NamespaceRole.class);
            assertThat(namespaceRole.namespace().value()).isEqualTo(NAMESPACE_VALUE);
            assertThat(namespaceRole.role()).isEqualTo(ROLE);
        }

        @Test
        @DisplayName("NamespaceRole로 생성한다")
        void create_from_namespace_role() {
            var namespaceRole = EmbeddableNamespaceRole.from(NAMESPACE_ROLE);

            assertThat(namespaceRole).isInstanceOf(NamespaceRole.class);
            assertThat(namespaceRole.namespace().value()).isEqualTo(NAMESPACE_VALUE);
            assertThat(namespaceRole.role()).isEqualTo(ROLE);
        }

        @ParameterizedTest(name = "namespace = {0}")
        @NullSource
        @DisplayName("namespace가 null이면 예외")
        void throw_exception_when_null_namespace(ApplicationNamespace namespace) {
            assertThatDomainThrownBy(() -> EmbeddableNamespaceRole.of(namespace, ROLE))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("namespace");
        }

        @ParameterizedTest(name = "namespace value = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("namespace value가 빈 문자열이면 예외")
        void throw_exception_when_blank_namespace_value(String value) {
            ApplicationNamespace namespace = () -> value;

            assertThatDomainThrownBy(() -> EmbeddableNamespaceRole.of(namespace, ROLE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("namespace");
        }

        @ParameterizedTest(name = "role = {0}")
        @NullSource
        @DisplayName("role이 null이면 예외")
        void throw_exception_when_null_role(Role role) {
            assertThatDomainThrownBy(() -> EmbeddableNamespaceRole.of(NAMESPACE, role))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("role");
        }

        @ParameterizedTest(name = "namespaceRole = {0}")
        @NullSource
        @DisplayName("NamespaceRole이 null이면 예외")
        void throw_exception_when_null_namespace_role(NamespaceRole namespaceRole) {
            assertThatDomainThrownBy(() -> EmbeddableNamespaceRole.from(namespaceRole))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("namespaceRole");
        }
    }

    @Nested
    @DisplayName("role 변경 테스트")
    class WithRoleTest {
        @Test
        @DisplayName("role만 변경한 새 EmbeddableNamespaceRole을 생성한다")
        void create_new_namespace_role_with_updated_role() {
            var namespaceRole = EmbeddableNamespaceRole.from(NAMESPACE_ROLE);

            var updated = namespaceRole.withRole(UPDATED_ROLE);

            assertThat(updated.namespace().value()).isEqualTo(NAMESPACE_VALUE);
            assertThat(updated.role()).isEqualTo(UPDATED_ROLE);
            assertThat(namespaceRole.role()).isEqualTo(ROLE);
        }

        @ParameterizedTest(name = "role = {0}")
        @NullSource
        @DisplayName("변경할 role이 null이면 예외")
        void throw_exception_when_update_role_is_null(Role role) {
            var namespaceRole = EmbeddableNamespaceRole.from(NAMESPACE_ROLE);

            assertThatDomainThrownBy(() -> namespaceRole.withRole(role))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("role");
        }
    }
}
