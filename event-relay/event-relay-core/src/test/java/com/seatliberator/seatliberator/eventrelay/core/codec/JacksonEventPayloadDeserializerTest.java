package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.codec.exception.EventPayloadDeserializationException;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("역직렬화 중 Jackson 예외가 발생하면 EventPayloadDeserializationException으로 번역한다")
    void materialize_translateException() {
        // given
        var failingObjectMapper = new ObjectMapper() {
            @Override
            public <T> T readValue(String content, Class<T> valueType) {
                throw new tools.jackson.core.JacksonException("deserialize fail") {
                };
            }
        };
        var deserializer = new JacksonEventPayloadDeserializer(failingObjectMapper);

        // when // then
        assertThatThrownBy(() ->
                deserializer.materialize(EventTestFixture.createRawPayload(), EventTestFixture.TestPayload.class)
        )
                .isInstanceOf(EventPayloadDeserializationException.class)
                .hasMessageContaining("Event Payload 역직렬화에 실패했습니다.")
                .hasCauseInstanceOf(tools.jackson.core.JacksonException.class);
    }
}
