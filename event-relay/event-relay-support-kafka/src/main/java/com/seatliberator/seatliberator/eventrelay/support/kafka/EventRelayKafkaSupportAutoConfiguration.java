package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeDeserializer;
import com.seatliberator.seatliberator.eventrelay.core.codec.EventEnvelopeSerializer;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.provider.ProducerProvider;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListenerRegistry;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.time.Clock;

@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
@AutoConfigureAfter(name = {
        "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration",
        "com.seatliberator.seatliberator.eventrelay.core.EventRelayCoreAutoConfiguration"
})
@EnableConfigurationProperties(EventRelayKafkaSupportConfigurationProperties.class)
public class EventRelayKafkaSupportAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventSender.class)
    KafkaEventSender eventSender(
            KafkaTemplate<String, String> kafkaTemplate,
            EventDefinitionRegistry eventDefinitionRegistry,
            EventEnvelopeSerializer eventEnvelopeSerializer,
            TopicFactory topicFactory,
            EventRelayKafkaSupportConfigurationProperties properties
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
    TopicFactory topicFactory(EventRelayKafkaSupportConfigurationProperties properties) {
        return new NamespaceTopicFactory(properties.topicSuffix(), properties.topicDelimiter());
    }
}
