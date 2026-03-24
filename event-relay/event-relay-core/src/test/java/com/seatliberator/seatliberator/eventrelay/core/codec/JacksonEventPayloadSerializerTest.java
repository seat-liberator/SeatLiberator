package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.codec.exception.EventEnvelopeSerializationException;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.factory.EventEnvelopeFactory;
import com.seatliberator.seatliberator.eventrelay.core.model.factory.ImmutableEventEnvelopeFactory;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Jackson Event Payload Serializer")
public class JacksonEventPayloadSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventPayloadSerializer serializer = new JacksonEventPayloadSerializer(objectMapper);
    private final EventEnvelopeFactory factory = new ImmutableEventEnvelopeFactory();

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

    @Test
    @DisplayName("직렬화 중 Jackson 예외가 발생하면 EventEnvelopeSerializationException으로 번역한다")
    void stringify_translateException() {
        // given
        var failingObjectMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) {
                throw new tools.jackson.core.JacksonException("serialize fail") {
                };
            }
        };
        var serializer = new JacksonEventEnvelopeSerializer(failingObjectMapper);
        EventEnvelope envelope = EventTestFixture.createEnvelope(factory);

        // when // then
        assertThatThrownBy(() -> serializer.stringify(envelope))
                .isInstanceOf(EventEnvelopeSerializationException.class)
                .hasMessageContaining("Event Envelope 직렬화에 실패했습니다.")
                .hasCauseInstanceOf(tools.jackson.core.JacksonException.class);
    }
}
