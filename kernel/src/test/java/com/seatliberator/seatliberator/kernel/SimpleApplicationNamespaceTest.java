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
        var namespace = SimpleApplicationNamespace.from(TEST_NAMESPACE);

        assertThat(namespace.value()).isEqualTo(TEST_NAMESPACE);
        assertThat(namespace).isInstanceOf(ApplicationNamespace.class);
    }

    @Test
    @DisplayName("공백이나 null을 전달하면 예외가 발생한다")
    void throw_if_null_or_blank_value() {
        assertThatThrownBy(() -> SimpleApplicationNamespace.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be blank.");

        assertThatThrownBy(() -> SimpleApplicationNamespace.from(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be blank.");

        assertThatThrownBy(() -> SimpleApplicationNamespace.from("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("application namespace must not be blank.");
    }
}
