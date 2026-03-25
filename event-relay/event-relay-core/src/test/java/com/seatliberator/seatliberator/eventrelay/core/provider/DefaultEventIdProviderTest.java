package com.seatliberator.seatliberator.eventrelay.core.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Default Event Id Provider")
public class DefaultEventIdProviderTest {

    @Test
    @DisplayName("IdGenerator가 생성한 값을 그대로 반환한다")
    void get() {
        IdGenerator generator = () -> "event-1";

        var provider = new DefaultEventIdProvider(generator);

        var actual = provider.get();

        assertThat(actual).isEqualTo("event-1");
    }
}
