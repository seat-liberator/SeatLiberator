package com.seatliberator.seatliberator.eventrelay.core.codec;

import com.seatliberator.seatliberator.eventrelay.core.model.factory.EventEnvelopeFactory;
import com.seatliberator.seatliberator.eventrelay.core.model.factory.ImmutableEventEnvelopeFactory;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Jackson Event Envelope Serializer")
public class JacksonEventEnvelopeSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventEnvelopeSerializer serializer = new JacksonEventEnvelopeSerializer(objectMapper);
    private final EventEnvelopeFactory factory = new ImmutableEventEnvelopeFactory();

    @Test
    @DisplayName("EventEnvelope를 JSON 문자열로 직렬화한다")
    void stringify() {
        // given
        var envelope = EventTestFixture.createEnvelope(factory);

        // when
        String actual = serializer.stringify(envelope);

        // then
        JsonNode root = objectMapper.readTree(actual);

        assertThat(root.get("header")).isNotNull();
        assertThat(root.get("trace")).isNotNull();
        assertThat(root.get("rawPayload")).isNotNull();

        assertThat(root.get("header").get("eventType").get("name").asString())
                .isEqualTo("reservation.create");

        assertThat(root.get("trace").get("eventId").asString())
                .isEqualTo("event-1");
        assertThat(root.get("trace").get("causationId").asString())
                .isEqualTo("parent-event-1");
        assertThat(root.get("trace").get("producer").asString())
                .isEqualTo("reservation-service");
        assertThat(root.get("trace").get("correlationId").asString())
                .isEqualTo("correlation-1");
        assertThat(root.get("trace").get("createdAt").asString())
                .isEqualTo("2026-01-01T00:00:00Z");

        assertThat(root.get("trace").get("aggregateDescriptor").get("type").asString())
                .isEqualTo("reservation");
        assertThat(root.get("trace").get("aggregateDescriptor").get("id").asString())
                .isEqualTo("reservation-1");

        JsonNode rawPayload = objectMapper.readTree(root.get("rawPayload").asString());
        assertThat(rawPayload.get("reservationId").asString())
                .isEqualTo("reservation-1");
        assertThat(rawPayload.get("seatId").asString())
                .isEqualTo("seat-1");
        assertThat(rawPayload.get("userId").asString())
                .isEqualTo("user-1");
    }
}
