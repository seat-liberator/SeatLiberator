package com.seatliberator.seatliberator.eventrelay.core.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Default Correlation Id Provider")
public class DefaultCorrelationIdProviderTest {

    @Test
    @DisplayName("IdGenerator가 생성한 값을 그대로 반환한다")
    void get() {
        IdGenerator generator = () -> "correlation-1";

        var provider = new DefaultCorrelationIdProvider(generator);

        var actual = provider.get();

        assertThat(actual).isEqualTo("correlation-1");
    }
}
