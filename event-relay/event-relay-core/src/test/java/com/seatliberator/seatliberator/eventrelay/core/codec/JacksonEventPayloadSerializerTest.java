package com.seatliberator.seatliberator.eventrelay.core.codec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Jackson Event Payload Serializer")
public class JacksonEventPayloadSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventPayloadSerializer serializer = new JacksonEventPayloadSerializer(objectMapper);

    @Test
    @DisplayName("EventPayload 구현체를 JSON 문자열로 직렬화한다")
    void stringify() {
        // given
        var payload = createPayload();

        // when
        String actual = serializer.stringify(payload);

        // then
        assertThat(actual).contains(getAggregateId());
        assertThat(actual).contains(getSeatFieldValue());
        assertThat(actual).contains(getUserFieldValue());
    }
}
