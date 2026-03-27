package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.codec.exception.EventPayloadSerializationException;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture.createPayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Jackson Event Payload Round-trip Serialization")
public class JacksonEventPayloadSerializationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JacksonEventPayloadSerializer serializer =
            new JacksonEventPayloadSerializer(objectMapper);
    private final JacksonEventPayloadDeserializer deserializer =
            new JacksonEventPayloadDeserializer(objectMapper);

    @Test
    @DisplayName("직렬화한 payload를 다시 같은 타입으로 역직렬화할 수 있다")
    void roundTrip() {
        // given
        var payload = createPayload();

        // when
        String raw = serializer.stringify(payload);
        var restored = deserializer.materialize(raw, EventTestFixture.TestPayload.class);

        // then
        assertThat(restored).isEqualTo(payload);
    }

    @Test
    @DisplayName("직렬화 중 Jackson 예외가 발생하면 EventPayloadSerializationException으로 번역한다")
    void stringify_translateException() {
        // given
        var failingObjectMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) {
                throw new tools.jackson.core.JacksonException("serialize fail") {
                };
            }
        };
        var serializer = new JacksonEventPayloadSerializer(failingObjectMapper);
        var payload = EventTestFixture.createPayload();

        // when // then
        assertThatThrownBy(() -> serializer.stringify(payload))
                .isInstanceOf(EventPayloadSerializationException.class)
                .hasMessageContaining("Event Payload 직렬화에 실패했습니다.")
                .hasCauseInstanceOf(tools.jackson.core.JacksonException.class);
    }
}
