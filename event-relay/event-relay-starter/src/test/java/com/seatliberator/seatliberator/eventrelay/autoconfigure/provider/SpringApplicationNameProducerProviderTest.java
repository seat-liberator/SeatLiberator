package com.seatliberator.seatliberator.eventrelay.autoconfigure.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Spring Application Name Producer Provider")
public class SpringApplicationNameProducerProviderTest {

    @Test
    @DisplayName("spring.application.name 프로퍼티가 있으면 해당 값을 반환한다")
    void get_property() {
        var env = new MockEnvironment()
                .withProperty("spring.application.name", "reservation-service");
        var provider = new SpringApplicationNameProducerProvider(env);

        var actual = provider.get();

        assertThat(actual).isEqualTo("reservation-service");
    }

    @Test
    @DisplayName("spring.application.name 프로퍼티가 없으면 unknown-service를 반환한다")
    void get_without_property() {
        var env = new MockEnvironment();
        var provider = new SpringApplicationNameProducerProvider(env);

        var actual = provider.get();

        assertThat(actual).isEqualTo("unknown-service");
    }

    @Test
    @DisplayName("spring.application.name 프로퍼티가 비어 있으면 unknown-service를 반환한다")
    void get_blankProperty() {
        var environment = new MockEnvironment()
                .withProperty("spring.application.name", "   ");
        var provider = new SpringApplicationNameProducerProvider(environment);

        var actual = provider.get();

        assertThat(actual).isEqualTo("unknown-service");
    }
}
