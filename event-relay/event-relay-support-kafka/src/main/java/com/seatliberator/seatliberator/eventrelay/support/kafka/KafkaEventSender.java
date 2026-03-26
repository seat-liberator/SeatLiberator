package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeSerializer;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEventSender implements EventSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventDefinitionRegistry definitionRegistry;
    private final EventEnvelopeSerializer serializer;
    private final TopicFactory topicFactory;

    public KafkaEventSender(
            KafkaTemplate<String, String> kafkaTemplate,
            EventDefinitionRegistry definitionRegistry,
            EventEnvelopeSerializer serializer,
            TopicFactory topicFactory
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.definitionRegistry = definitionRegistry;
        this.serializer = serializer;
        this.topicFactory = topicFactory;
    }

    @Override
    public void send(@NonNull EventEnvelope envelope) {
        var type = envelope.header().eventType();
        var definition = definitionRegistry.resolve(type);
        if (definition == null) {
            throw new IllegalStateException("Missing definition type: " + type.name());
        }
        var topic = topicFactory.fromRegistration(definition);
        var data = serializer.stringify(envelope);
        var key = envelope.trace().eventId();

        kafkaTemplate.send(topic, key, data);
    }
}
