package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeSerializer;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

public class KafkaEventSender implements EventSender {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventSender.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventDefinitionRegistry definitionRegistry;
    private final EventEnvelopeSerializer serializer;
    private final TopicFactory topicFactory;
    private final long sendTimeout;

    public KafkaEventSender(
            KafkaTemplate<String, String> kafkaTemplate,
            EventDefinitionRegistry definitionRegistry,
            EventEnvelopeSerializer serializer,
            TopicFactory topicFactory,
            long sendTimeout
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.definitionRegistry = definitionRegistry;
        this.serializer = serializer;
        this.topicFactory = topicFactory;
        this.sendTimeout = sendTimeout;
    }

    @Override
    public void send(@NonNull EventEnvelope envelope) throws Exception {
        var type = envelope.header().eventType();
        var definition = definitionRegistry.resolve(type);
        if (definition == null) {
            throw new IllegalStateException("Missing definition type: " + type.name());
        }
        var topic = topicFactory.fromRegistration(definition);
        var data = serializer.stringify(envelope);
        var key = envelope.trace().eventId();

        log.debug("event send. topic={}, key={}, data={}", topic, key, data);

        kafkaTemplate.send(topic, key, data).get(sendTimeout, TimeUnit.SECONDS);
    }
}
