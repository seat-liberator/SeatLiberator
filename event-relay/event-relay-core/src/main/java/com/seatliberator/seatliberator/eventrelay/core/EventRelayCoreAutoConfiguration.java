package com.seatliberator.seatliberator.eventrelay.core;

import com.seatliberator.seatliberator.eventrelay.core.codec.*;
import com.seatliberator.seatliberator.eventrelay.core.definition.DefaultEventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionCollection;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistrar;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionRegistry;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceFactory;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.factory.ImmutableEventTraceFactory;
import com.seatliberator.seatliberator.eventrelay.core.factory.ThreadLocalEventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.provider.*;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventRelay;
import com.seatliberator.seatliberator.eventrelay.core.relay.SimpleEventRelay;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.*;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.DefaultEventPublisher;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import com.seatliberator.seatliberator.eventrelay.core.scheduler.EventScheduler;
import com.seatliberator.seatliberator.eventrelay.core.scheduler.FixedDelayEventScheduler;
import com.seatliberator.seatliberator.eventrelay.core.store.DefaultEventStateTransitionPolicy;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStateTransitionPolicy;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStoreConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@AutoConfiguration
@EnableConfigurationProperties(EventStoreConfigurationProperties.class)
@EnableScheduling
public class EventRelayCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventScheduler.class)
    FixedDelayEventScheduler eventScheduler(EventRelay eventRelay) {
        return new FixedDelayEventScheduler(eventRelay);
    }

    @Bean
    @ConditionalOnMissingBean(EventRelay.class)
    SimpleEventRelay eventRelay(
            EventRouter eventRouter,
            EventSender eventSender,
            EventStore eventStore,
            Clock clock
    ) {
        return new SimpleEventRelay(clock, eventStore, eventRouter, eventSender);
    }

    @Bean
    @ConditionalOnMissingBean(EventRouter.class)
    RegistryEventRouter eventRouter(
            EventListenerRegistry eventListenerRegistry,
            EventPayloadDeserializer eventPayloadDeserializer,
            EventTraceHolder eventTraceHolder
    ) {
        return new RegistryEventRouter(eventListenerRegistry, eventPayloadDeserializer, eventTraceHolder);
    }

    @Bean
    @ConditionalOnMissingBean(EventPublisher.class)
    DefaultEventPublisher eventPublisher(
            EventPayloadSerializer eventPayloadSerializer,
            EventStore eventStore,
            EventTraceFactory eventTraceFactory,
            Clock clock
    ) {
        return new DefaultEventPublisher(eventPayloadSerializer, eventTraceFactory, eventStore, clock);
    }

    @Bean
    @ConditionalOnMissingBean(EventStateTransitionPolicy.class)
    DefaultEventStateTransitionPolicy eventStateTransitionPolicy() {
        return new DefaultEventStateTransitionPolicy();
    }

    @Bean
    @ConditionalOnMissingBean(EventTraceFactory.class)
    ImmutableEventTraceFactory eventTraceFactory(
            EventTraceHolder eventTraceHolder,
            EventIdProvider eventIdProvider,
            CorrelationIdProvider correlationIdProvider,
            ProducerProvider producerProvider,
            Clock clock
    ) {
        return new ImmutableEventTraceFactory(eventTraceHolder, eventIdProvider, producerProvider, correlationIdProvider, clock);
    }

    @Bean
    @ConditionalOnMissingBean(EventTraceHolder.class)
    ThreadLocalEventTraceHolder eventTraceHolder() {
        return new ThreadLocalEventTraceHolder();
    }

    @Bean
    @ConditionalOnMissingBean(EventEnvelopeDeserializer.class)
    JacksonEventEnvelopeDeserializer jacksonEventEnvelopeDeserializer(ObjectMapper objectMapper) {
        return new JacksonEventEnvelopeDeserializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(EventEnvelopeSerializer.class)
    JacksonEventEnvelopeSerializer eventEnvelopeSerializer(ObjectMapper objectMapper) {
        return new JacksonEventEnvelopeSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(EventPayloadSerializer.class)
    JacksonEventPayloadSerializer eventPayloadSerializer(ObjectMapper objectMapper) {
        return new JacksonEventPayloadSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(EventPayloadDeserializer.class)
    JacksonEventPayloadDeserializer eventPayloadDeserializer(ObjectMapper objectMapper) {
        return new JacksonEventPayloadDeserializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean({
            EventListenerRegistry.class,
            EventListenerRegistrar.class
    })
    DefaultEventListenerRegistry defaultEventListenerRegistry(List<EventListener<?>> listeners) {
        return new DefaultEventListenerRegistry(listeners);
    }

    @Bean
    @ConditionalOnMissingBean({
            EventDefinitionRegistry.class,
            EventDefinitionRegistrar.class
    })
    DefaultEventDefinitionRegistry defaultEventDefinitionRegistry(List<EventDefinitionCollection> collections) {
        return new DefaultEventDefinitionRegistry(collections);
    }

    @Bean
    @ConditionalOnMissingBean(EventIdProvider.class)
    DefaultEventIdProvider eventIdProvider(IdGenerator idGenerator) {
        return new DefaultEventIdProvider(idGenerator);
    }

    @Bean
    @ConditionalOnMissingBean(CorrelationIdProvider.class)
    DefaultCorrelationIdProvider correlationIdProvider(IdGenerator idGenerator) {
        return new DefaultCorrelationIdProvider(idGenerator);
    }

    @Bean
    @ConditionalOnMissingBean(ProducerProvider.class)
    SpringApplicationNameProducerProvider producerProvider(Environment environment) {
        return new SpringApplicationNameProducerProvider(environment);
    }

    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    IdGenerator idGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock clock() {
        return Clock.systemUTC();
    }
}
