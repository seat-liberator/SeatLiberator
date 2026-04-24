package com.seatliberator.seatliberator.kernel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FixedCurrentApplicationNamespaceProvider")
public class FixedCurrentApplicationNamespaceProviderTest {
    private static final String TEST_NAMESPACE = "test-application";

    @Test
    @DisplayName("ApplicationNamespace를 전달해서 Provider를 생성한다")
    void can_create_provider_with_application_namespace() {
        var namespace = SimpleApplicationNamespace.of(TEST_NAMESPACE);

        var provider = new FixedCurrentApplicationNamespaceProvider(namespace);

        assertThat(provider).isInstanceOf(CurrentApplicationNamespaceProvider.class);
        assertThat(provider.current()).isEqualTo(namespace);
    }

    @Test
    @DisplayName("문자열 namespace를 전달해서 Provider를 생성한다")
    void can_create_provider_with_string_namespace() {
        var provider = new FixedCurrentApplicationNamespaceProvider(TEST_NAMESPACE);

        assertThat(provider).isInstanceOf(CurrentApplicationNamespaceProvider.class);
        assertThat(provider.current().value()).isEqualTo(TEST_NAMESPACE);
    }

    @Test
    @DisplayName("null ApplicationNamespace를 전달하면 예외가 발생한다")
    void throw_if_null_or_blank_value() {
        assertThatThrownBy(() -> new FixedCurrentApplicationNamespaceProvider((ApplicationNamespace) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("current application namespace must not be null.");

        assertThatThrownBy(() -> new FixedCurrentApplicationNamespaceProvider((String) null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
