package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.provider.ProducerProvider;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListenerRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.util.Objects;

public class KafkaDynamicEventSubscriber {
    private final EventListenerRegistry listenerRegistry;
    private final EventDefinitionRegistry definitionRegistry;
    private final ProducerProvider producerProvider;
    private final TopicFactory topicFactory;
    private final EndpointHandler endpointHandler;
    private final MessageHandlerMethodFactory methodFactory;
    private final KafkaListenerEndpointRegistry endpointRegistry;
    private final ConcurrentKafkaListenerContainerFactory<String, String> containerFactory;

    public KafkaDynamicEventSubscriber(
            @NonNull EventListenerRegistry listenerRegistry,
            @NonNull EventDefinitionRegistry definitionRegistry,
            @NonNull ProducerProvider producerProvider,
            @NonNull TopicFactory topicFactory,
            @NonNull EndpointHandler endpointHandler,
            @NonNull MessageHandlerMethodFactory methodFactory,
            @NonNull KafkaListenerEndpointRegistry endpointRegistry,
            @NonNull ConcurrentKafkaListenerContainerFactory<String, String> containerFactory
    ) {
        this.listenerRegistry = listenerRegistry;
        this.definitionRegistry = definitionRegistry;
        this.producerProvider = producerProvider;
        this.topicFactory = topicFactory;
        this.endpointHandler = endpointHandler;
        this.methodFactory = methodFactory;
        this.endpointRegistry = endpointRegistry;
        this.containerFactory = containerFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterBoot() throws NoSuchMethodException {
        var listenerEventType = listenerRegistry.listAll().stream()
                .map(l -> l.definition().type())
                .toList();

        var topics = listenerEventType.stream()
                .map(definitionRegistry::resolve)
                .filter(Objects::nonNull)
                .map(topicFactory::fromRegistration)
                .distinct()
                .toList();

        if (topics.isEmpty()) return;

        var endpoint = new MethodKafkaListenerEndpoint<String, String>();
        endpoint.setId("event-dynamic-subscriber");
        endpoint.setGroupId(producerProvider.get());
        endpoint.setTopics(topics.toArray(String[]::new));

        endpoint.setBean(endpointHandler);
        endpoint.setMessageHandlerMethodFactory(methodFactory);
        endpoint.setMethod(DefaultEndpointHandler.class.getMethod("onMessage", ConsumerRecord.class));

        endpointRegistry.registerListenerContainer(endpoint, containerFactory, true);
    }
}
