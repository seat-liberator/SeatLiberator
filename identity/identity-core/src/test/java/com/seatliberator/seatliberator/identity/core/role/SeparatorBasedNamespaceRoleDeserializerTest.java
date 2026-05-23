package com.seatliberator.seatliberator.identity.core.role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DisplayName("SeparatorBasedNamespaceRoleDeserializer 테스트")
public class SeparatorBasedNamespaceRoleDeserializerTest {

    NamespaceRoleDeserializer deserializer;

    @BeforeEach
    void run() {
        deserializer = new SeparatorBasedNamespaceRoleDeserializer();
    }

    @ParameterizedTest(name = "raw = {0}")
    @ValueSource(strings = {
            "ns",
            "ns:",
            ":USER",
            "ns:USER:EXTRA"
    })
    @DisplayName("올바른 namespace-role 형식이 아니면 예외를 던진다")
    void materialize_invalid_namespaceRole_format(String raw) {
        assertThatThrownBy(() -> deserializer.materialize(raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid namespace role format.");
    }

    @DisplayName("raw가 null이면 예외를 던진다")
    void materialize_null_raw(String raw) {
        assertThatDomainThrownBy(() -> deserializer.materialize((String) null))
                .hasNonNullMessageFor("raw");
    }

    @DisplayName("raw가 비어있으면 예외를 던진다")
    void materialize_blank_raw(String raw) {
        assertThatDomainThrownBy(() -> deserializer.materialize("   "))
                .hasNonBlankMessageFor("raw");
    }

    @Test
    @DisplayName("존재하지 않는 role이면 예외를 던진다")
    void materialize_unknown_role() {
        assertThatThrownBy(() -> deserializer.materialize("ns:UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid role 'UNKNOWN'");
    }

    @Test
    @DisplayName("separator로 구분된 문자열 컬렉션에서 NamespaceRole Set을 만든다")
    void materialize_namespaceRole_collection() {
        var result = deserializer.materialize(List.of(
                "reservation:USER",
                "room:ADMIN"
        ));

        assertThat(result)
                .hasSize(2)
                .extracting(
                        namespaceRole -> namespaceRole.namespace().value(),
                        NamespaceRole::role
                )
                .containsExactlyInAnyOrder(
                        tuple("reservation", Role.USER),
                        tuple("room", Role.ADMIN)
                );
    }

    @Test
    @DisplayName("중복된 namespace-role 문자열은 하나의 NamespaceRole로 변환된다")
    void materialize_duplicate_namespaceRole_collection() {
        var result = deserializer.materialize(List.of(
                "reservation:USER",
                "reservation:USER"
        ));

        assertThat(result).hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("rawCollection이 null이면 예외를 던진다")
    void materialize_null_collection(Collection<String> rawCollection) {
        assertThatThrownBy(() -> deserializer.materialize(rawCollection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("rawCollection must not be null.");
    }

    @Test
    @DisplayName("빈 컬렉션이면 빈 Set을 반환한다")
    void materialize_empty_collection() {
        var result = deserializer.materialize(List.of());

        assertThat(result).isEmpty();
    }

    private String createTestRaw(String namespace, String role, String separator) {
        return namespace + separator + role;
    }
}
