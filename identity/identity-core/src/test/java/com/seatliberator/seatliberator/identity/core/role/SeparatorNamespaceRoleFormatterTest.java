package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SeparatorNamespaceRoleFormatter 테스트")
public class SeparatorNamespaceRoleFormatterTest {
    @ParameterizedTest(name = "separator = \"{0}\"")
    @ValueSource(strings = {":", ".", ",", "-", "|", "+"})
    @DisplayName("NamespaceRole을 separator로 구분된 문자열로 변환한다")
    void format_namespaceRole(String separator) {
        var namespaceRole = SimpleNamespaceRole.from(
                SimpleApplicationNamespace.of("ns"),
                Role.USER
        );

        var formatter = new SeparatorNamespaceRoleFormatter(separator);

        var result = formatter.format(namespaceRole);

        assertThat(result).isEqualTo("ns" + separator + "USER");
    }

    @Test
    @DisplayName("NamespaceRole 컬렉션을 문자열 Set으로 변환한다")
    void format_namespaceRole_collection() {
        var formatter = new SeparatorNamespaceRoleFormatter(":");

        var result = formatter.format(List.of(
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
    void format_null_namespaceRole() {
        var formatter = new SeparatorNamespaceRoleFormatter(":");

        assertThatThrownBy(() -> formatter.format((NamespaceRole) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("namespaceRole must not be null.");
    }

    @Test
    @DisplayName("컬렉션 원소가 null이면 예외를 던진다")
    void format_collection_contains_null_element() {
        var formatter = new SeparatorNamespaceRoleFormatter(":");

        assertThatThrownBy(() -> formatter.format(Arrays.asList(
                SimpleNamespaceRole.from(SimpleApplicationNamespace.of("reservation"), Role.USER),
                null
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("namespaceRole must not be null.");
    }

    @Test
    @DisplayName("빈 컬렉션이면 빈 Set을 반환한다")
    void format_empty_collection() {
        var formatter = new SeparatorNamespaceRoleFormatter(":");

        var result = formatter.format(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("namespace 값에 separator가 포함되어 있으면 예외를 던진다")
    void format_namespace_contains_separator() {
        var formatter = new SeparatorNamespaceRoleFormatter(":");

        var namespaceRole = SimpleNamespaceRole.from(
                SimpleApplicationNamespace.of("reservation:internal"),
                Role.USER
        );

        assertThatThrownBy(() -> formatter.format(namespaceRole))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("namespace value must not contain separator");
    }
}
