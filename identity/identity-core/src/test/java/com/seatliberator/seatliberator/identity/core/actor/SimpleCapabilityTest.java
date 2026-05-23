package com.seatliberator.seatliberator.identity.core.actor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleCapability 테스트")
public class SimpleCapabilityTest {
    @Test
    @DisplayName("scope와 description으로 생성")
    void create_with_scope_and_description() {
        var scope = "test.scope";
        var description = "test capability description";

        var capability = SimpleCapability.of(scope, description);

        assertThat(capability.scope()).isEqualTo(scope);
        assertThat(capability.description()).isEqualTo(description);
    }

    @Test
    @DisplayName("유효하지 않은 scope와 description을 전달하면 예외")
    void throw_exception_when_scope_and_description_is_invalid() {
        var scope = "test.scope";
        var description = "test capability description";

        assertThatDomainThrownBy(() -> SimpleCapability.of(null, description))
                .hasNonNullMessageFor("scope");

        assertThatDomainThrownBy(() -> SimpleCapability.of("  ", description))
                .hasNonBlankMessageFor("scope");

        assertThatDomainThrownBy(() -> SimpleCapability.of(scope, null))
                .hasNonNullMessageFor("description");

        assertThatDomainThrownBy(() -> SimpleCapability.of(scope, "  "))
                .hasNonBlankMessageFor("description");
    }

    @Test
    @DisplayName("다른 Capability 구현체에서 변환 가능")
    void convert_from_other_implementation() {
        var capability = SimpleCapability.from(TestCapability.TEST_CAPABILITY);

        assertThat(capability.scope()).isEqualTo(TestCapability.TEST_CAPABILITY.scope());
        assertThat(capability.description()).isEqualTo(TestCapability.TEST_CAPABILITY.description());
    }

    @Test
    @DisplayName("변환 시 다른 구현체가 null이면 예외")
    void throw_exception_when_capability_is_null() {
        assertThatDomainThrownBy(() -> SimpleCapability.from(null))
                .hasNonNullMessageFor("capability");
    }

    @Test
    @DisplayName("변환 시 다른 구현체 필드가 유효하지 않으면 예외")
    void throw_exception_when_source_capability_is_invalid() {
        Capability invalidScope = new Capability() {
            @Override
            public String scope() {
                return "  ";
            }

            @Override
            public String description() {
                return "description";
            }
        };

        assertThatDomainThrownBy(() -> SimpleCapability.from(invalidScope))
                .hasNonBlankMessageFor("scope");

        Capability invalidDescription = new Capability() {
            @Override
            public String scope() {
                return "scope";
            }

            @Override
            public String description() {
                return "  ";
            }
        };

        assertThatDomainThrownBy(() -> SimpleCapability.from(invalidDescription))
                .hasNonBlankMessageFor("description");
    }
}
