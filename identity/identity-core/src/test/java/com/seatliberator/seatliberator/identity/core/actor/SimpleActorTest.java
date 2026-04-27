package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.TestCapability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimpleActorTest {
    @Test
    @DisplayName("subject가 유효하지 않으면 예외")
    void throw_exception_when_subject_invalid() {
        assertThatThrownBy(() -> SimpleActor.of(null, Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("subject must not be null or blank.");

        assertThatThrownBy(() -> SimpleActor.of("  ", Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("subject must not be null or blank.");
    }

    @Test
    @DisplayName("capabilities가 null이면 예외")
    void throw_exception_when_capabilities_null() {
        var subject = "user-1";

        assertThatThrownBy(() -> SimpleActor.of(subject, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("capabilities must not be null.");
    }

    @Test
    @DisplayName("subject와 capabilities로 생성한다")
    void create_with_subject_and_capabilities() {
        var subject = "user-1";
        var capabilities = Set.of(TestCapability.TEST_CAPABILITY);

        var actor = SimpleActor.of(subject, capabilities);

        assertThat(actor.subject()).isEqualTo(subject);
        assertThat(actor.capabilities())
                .containsExactly(TestCapability.TEST_CAPABILITY);
    }
}
