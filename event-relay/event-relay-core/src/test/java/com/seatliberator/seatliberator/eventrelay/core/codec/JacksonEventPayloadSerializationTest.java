package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture.createPayload;
import static org.assertj.core.api.Assertions.assertThat;

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
}
