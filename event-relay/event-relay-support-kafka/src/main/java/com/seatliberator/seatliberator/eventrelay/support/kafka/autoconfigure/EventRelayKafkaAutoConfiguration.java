package com.seatliberator.seatliberator.eventrelay.support.kafka.autoconfigure;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeDeserializer;
import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeSerializer;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.provider.ProducerProvider;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListenerRegistry;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.support.kafka.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.time.Clock;

@AutoConfiguration
@AutoConfigureAfter(name = "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration")
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(
        prefix = "event-relay",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ConditionalOnProperty(
        prefix = "event-relay.kafka",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(EventRelayKafkaProperties.class)
public class EventRelayKafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventSender.class)
    KafkaEventSender eventSender(
            KafkaTemplate<String, String> kafkaTemplate,
            EventDefinitionRegistry eventDefinitionRegistry,
            EventEnvelopeSerializer eventEnvelopeSerializer,
            TopicFactory topicFactory,
            EventRelayKafkaProperties properties
    ) {
        return new KafkaEventSender(kafkaTemplate, eventDefinitionRegistry, eventEnvelopeSerializer, topicFactory, properties.sendTimeout());
    }

    @Bean
    @ConditionalOnBean({
            KafkaListenerEndpointRegistry.class,
            ConcurrentKafkaListenerContainerFactory.class
    })
    KafkaDynamicEventSubscriber kafkaDynamicEventSubscriber(
            EventListenerRegistry eventListenerRegistry,
            EventDefinitionRegistry eventDefinitionRegistry,
            ProducerProvider producerProvider,
            TopicFactory topicFactory,
            EndpointHandler endpointHandler,
            MessageHandlerMethodFactory messageHandlerMethodFactory,
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory
    ) {
        return new KafkaDynamicEventSubscriber(
                eventListenerRegistry,
                eventDefinitionRegistry,
                producerProvider,
                topicFactory,
                endpointHandler,
                messageHandlerMethodFactory,
                kafkaListenerEndpointRegistry,
                concurrentKafkaListenerContainerFactory
        );
    }

    @Bean
    @ConditionalOnMissingBean(EndpointHandler.class)
    EndpointHandler endpointHandler(
            EventStore eventStore,
            EventEnvelopeDeserializer eventEnvelopeDeserializer,
            Clock clock
    ) {
        return new DefaultEndpointHandler(eventStore, eventEnvelopeDeserializer, clock);
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        return new DefaultMessageHandlerMethodFactory();
    }

    @Bean
    @ConditionalOnMissingBean(TopicFactory.class)
    TopicFactory topicFactory(EventRelayKafkaProperties properties) {
        return new NamespaceTopicFactory(properties.topicSuffix(), properties.topicDelimiter());
    }
}
