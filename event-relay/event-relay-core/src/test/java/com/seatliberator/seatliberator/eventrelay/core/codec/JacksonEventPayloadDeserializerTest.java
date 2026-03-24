package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Jackson Event Payload Deserializer")
public class JacksonEventPayloadDeserializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventPayloadDeserializer deserializer = new JacksonEventPayloadDeserializer(objectMapper);

    @Test
    @DisplayName("JSON 문자열을 기대한 EventPayload 타입으로 역직렬화한다")
    void materialize() {
        // given
        String rawPayload = createRawPayload();

        // when
        var actual = deserializer.materialize(rawPayload, EventTestFixture.TestPayload.class);

        // then
        assertThat(actual.reservationId()).isEqualTo(getAggregateId());
        assertThat(actual.seatId()).isEqualTo(getSeatFieldValue());
        assertThat(actual.userId()).isEqualTo(getUserFieldValue());
    }
}
