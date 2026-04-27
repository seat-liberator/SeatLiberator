package com.seatliberator.seatliberator.kernel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SimpleApplicationNamespace")
public class SimpleApplicationNamespaceTest {
    private static final String TEST_NAMESPACE = "test-application";

    @Test
    @DisplayName("문자열 value를 전달해서 동일한 value의 SimpleApplicationNamespace를 생성한다")
    void can_create_with_string_value() {
        var namespace = SimpleApplicationNamespace.of(TEST_NAMESPACE);

        assertThat(namespace.value()).isEqualTo(TEST_NAMESPACE);
        assertThat(namespace).isInstanceOf(ApplicationNamespace.class);
    }

    @Test
    @DisplayName("ApplicationNamespace 인터페이스로 동일한 value의 SimpleApplicationNamespace를 생성한다")
    void can_create_with_application_namespace_interface() {
        var otherNamespace = new ApplicationNamespace() {
            @Override
            public String value() {
                return TEST_NAMESPACE;
            }
        };

        var namespace = SimpleApplicationNamespace.from(otherNamespace);

        assertThat(namespace.value()).isEqualTo(otherNamespace.value());
        assertThat(namespace).isInstanceOf(ApplicationNamespace.class);
    }

    @Test
    @DisplayName("공백이나 null을 전달하면 예외가 발생한다")
    void throw_if_null_or_blank_value() {
        assertThatThrownBy(() -> SimpleApplicationNamespace.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be blank.");

        assertThatThrownBy(() -> SimpleApplicationNamespace.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be blank.");

        assertThatThrownBy(() -> SimpleApplicationNamespace.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be blank.");

        assertThatThrownBy(() -> SimpleApplicationNamespace.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be null.");
    }

    @Test
    @DisplayName("value가 같으면 동등성 체크는 true")
    void true_if_same_value() {
        var nsValue = "ns";

        var ns1 = SimpleApplicationNamespace.of(nsValue);
        ApplicationNamespace ns2 = new ApplicationNamespace() {
            @Override
            public String value() {
                return nsValue;
            }
        };

        assertThat(ns1.isSame(ns2)).isTrue();
    }

    @Test
    @DisplayName("value가 다르면 동등성 체크는 false")
    void false_if_diff_value() {
        var ns1Value = "ns-1";
        var ns2Value = "ns-2";

        var ns1 = SimpleApplicationNamespace.of(ns1Value);
        var ns2 = SimpleApplicationNamespace.of(ns2Value);

        assertThat(ns1.isSame(ns2)).isFalse();
    }
}