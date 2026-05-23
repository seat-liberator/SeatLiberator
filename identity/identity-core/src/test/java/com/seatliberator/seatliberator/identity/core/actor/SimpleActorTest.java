package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.TestCapability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleActorTest {
    @Test
    @DisplayName("subject가 유효하지 않으면 예외")
    void throw_exception_when_subject_invalid() {
        assertThatDomainThrownBy(() -> SimpleActor.of(null, Set.of()))
                .hasNonNullMessageFor("subject");

        assertThatDomainThrownBy(() -> SimpleActor.of("  ", Set.of()))
                .hasNonBlankMessageFor("subject");
    }

    @Test
    @DisplayName("capabilities가 null이면 예외")
    void throw_exception_when_capabilities_null() {
        var subject = "user-1";

        assertThatDomainThrownBy(() -> SimpleActor.of(subject, null))
                .hasNonNullMessageFor("capabilities");
    }

    @Test
    @DisplayName("subject와 capabilities로 생성한다")
    void create_with_subject_and_capabilities() {
        var subject = "user-1";

        var actor = SimpleActor.of(subject, Set.of(TestCapability.TEST_CAPABILITY));

        assertThat(actor.subject()).isEqualTo(subject);
        assertThat(actor.capabilities())
                .containsExactly(TestCapability.TEST_CAPABILITY);
    }
}
