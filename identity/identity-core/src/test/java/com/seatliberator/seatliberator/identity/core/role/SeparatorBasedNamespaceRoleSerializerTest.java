package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SeparatorBasedNamespaceRoleSerializer 테스트")
public class SeparatorBasedNamespaceRoleSerializerTest {

    NamespaceRoleSerializer serializer;

    @BeforeEach
    void run() {
        serializer = new SeparatorBasedNamespaceRoleSerializer();
    }

    @Test
    @DisplayName("NamespaceRole 컬렉션을 문자열 Set으로 변환한다")
    void serialize_namespaceRole_collection() {
        var result = serializer.serialize(List.of(
                SimpleNamespaceRole.from(SimpleApplicationNamespace.of("reservation"), Role.USER),
                SimpleNamespaceRole.from(SimpleApplicationNamespace.of("room"), Role.ADMIN)
        ));

        assertThat(result)
                .containsExactlyInAnyOrder(
                        "reservation:USER",
                        "room:ADMIN"
                );
    }

    @Test
    @DisplayName("namespaceRole이 null이면 예외를 던진다")
    void serialize_null_namespaceRole() {
        assertThatDomainThrownBy(() -> serializer.serialize((NamespaceRole) null))
                .hasNonNullMessageFor("namespaceRole");
    }

    @Test
    @DisplayName("컬렉션 원소가 null이면 예외를 던진다")
    void serialize_collection_contains_null_element() {
        assertThatDomainThrownBy(() -> serializer.serialize(Arrays.asList(
                SimpleNamespaceRole.from(SimpleApplicationNamespace.of("reservation"), Role.USER),
                null
        )))
                .hasNonNullMessageFor("namespaceRole");
    }

    @Test
    @DisplayName("빈 컬렉션이면 빈 Set을 반환한다")
    void serialize_empty_collection() {
        var result = serializer.serialize(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("namespace 값에 separator가 포함되어 있으면 예외를 던진다")
    void serialize_namespace_contains_separator() {
        var namespaceRole = SimpleNamespaceRole.from(
                SimpleApplicationNamespace.of("reservation:internal"),
                Role.USER
        );

        assertThatThrownBy(() -> serializer.serialize(namespaceRole))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("namespace value must not contain separator");
    }
}
