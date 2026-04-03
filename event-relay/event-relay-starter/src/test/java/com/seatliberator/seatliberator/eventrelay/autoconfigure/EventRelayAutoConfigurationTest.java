package com.seatliberator.seatliberator.eventrelay.autoconfigure;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventRelay;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.NoOpEventSender;
import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.store.DefaultEventStore;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventType;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaEventStore;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEventPersistencePort;
import com.seatliberator.seatliberator.eventrelay.support.jpa.autoconfigure.EventRelayJpaAutoConfiguration;
import com.seatliberator.seatliberator.eventrelay.support.kafka.KafkaDynamicEventSubscriber;
import com.seatliberator.seatliberator.eventrelay.support.kafka.KafkaEventSender;
import com.seatliberator.seatliberator.eventrelay.support.kafka.autoconfigure.EventRelayKafkaAutoConfiguration;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.eventrelay.test.EventFixture;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Event Relay Auto Configuration")
public class EventRelayAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    EventRelayAutoConfiguration.class,
                    EventRelayJpaAutoConfiguration.class,
                    EventRelayKafkaAutoConfiguration.class
            ))
            .withPropertyValues("spring.application.name=test-app")
            .withUserConfiguration(TestSupport.class);

    @Nested
    @DisplayName("Core")
    class CoreOnly {
        @Test
        @DisplayName("event-relay.enabled=false면 모든 Bean이 안올라간다")
        void relay_disabled_disabled_everything() {
            contextRunner
                    .withPropertyValues("event-relay.enabled=false")
                    .run(context -> {
                        assertThat(context).doesNotHaveBean(EventRelay.class);
                        assertThat(context).doesNotHaveBean(EventStore.class);
                        assertThat(context).doesNotHaveBean(EventPublisher.class);
                        assertThat(context).doesNotHaveBean(EventSender.class);
                        assertThat(context).doesNotHaveBean(KafkaDynamicEventSubscriber.class);
                    });
        }

        @Test
        @DisplayName("아무 설정도 없으면 kafka, jpa support bean이 모두 올라온다")
        void missing_properties_for_default() {
            contextRunner.run(context -> {
                assertThat(context).hasSingleBean(EventRelay.class);
                assertThat(context)
                        .hasSingleBean(EventStore.class)
                        .getBean(EventStore.class)
                        .isInstanceOf(JpaEventStore.class);
                assertThat(context)
                        .hasSingleBean(EventSender.class)
                        .getBean(EventSender.class)
                        .isInstanceOf(KafkaEventSender.class);
            });
        }
    }

    @Nested
    @DisplayName("Kafka")
    class WithKafka {
        @Test
        @DisplayName("event-relay.kafka.enabled=false면 core만 올라온다")
        void kafka_disable() {
            contextRunner
                    .withPropertyValues("event-relay.kafka.enabled=false")
                    .run(context -> {
                        assertThat(context)
                                .hasSingleBean(EventSender.class)
                                .getBean(EventSender.class)
                                .isInstanceOf(NoOpEventSender.class);
                        assertThat(context).doesNotHaveBean(KafkaDynamicEventSubscriber.class);
                    });
        }

        @Test
        @DisplayName("event-relay.kafka.enabled=false면 outbound 이벤트는 실패 후 재시도 대상에 남는다")
        void outbound_event_is_retryable_when_kafka_disabled() {
            contextRunner
                    .withPropertyValues(
                            "event-relay.kafka.enabled=false",
                            "event-relay.jpa.enabled=false"
                    )
                    .run(context -> {
                        var eventPublisher = context.getBean(EventPublisher.class);
                        var eventRelay = context.getBean(EventRelay.class);
                        var eventStore = context.getBean(EventStore.class);
                        var eventTraceHolder = context.getBean(EventTraceHolder.class);
                        var clock = context.getBean(java.time.Clock.class);

                        eventTraceHolder.with(
                                () -> eventPublisher.publish(
                                        ImmutableEventType.from("reservation.create"),
                                        new EventFixture.TestEventPayload("title", "description")
                                ),
                                state -> state.withAggregate("reservation", "reservation-id")
                        );

                        eventRelay.run();

                        var claimed = eventStore.claimBatch(EventFlow.OUTBOUND, clock.instant(), 10);

                        assertThat(claimed).hasSize(1);
                    });
        }

        @Test
        @DisplayName("event-relay.kafka.enabled=false여도 jpa 는 올라온다")
        void jpa_enabled_when_kafka_disabled() {
            contextRunner
                    .withPropertyValues("event-relay.kafka.enabled=false")
                    .run(context -> {
                        assertThat(context)
                                .hasSingleBean(EventStore.class)
                                .getBean(EventStore.class)
                                .isInstanceOf(JpaEventStore.class);
                    });
        }

        @Test
        @DisplayName("event-relay.kafka.enabled=true면 kafka bean이 올라온다")
        void kafka_enabled() {
            contextRunner
                    .withPropertyValues("event-relay.kafka.enabled=true")
                    .run(context -> {
                        assertThat(context)
                                .hasSingleBean(EventSender.class)
                                .getBean(EventSender.class)
                                .isInstanceOf(KafkaEventSender.class);
                    });
        }
    }

    @Nested
    @DisplayName("With JPA")
    class WithJpa {
        @Test
        @DisplayName("event-relay.jpa.enabled=false면 코어만 올라온다")
        void jpa_disabled() {
            contextRunner
                    .withPropertyValues("event-relay.jpa.enabled=false")
                    .run(context -> {
                        assertThat(context)
                                .hasSingleBean(EventStore.class)
                                .getBean(EventStore.class)
                                .isInstanceOf(DefaultEventStore.class);
                    });
        }

        @Test
        @DisplayName("event-relay.jpa.enabled=false라도 kafka는 올라온다")
        void kafka_enabled_when_jpa_disabled() {
            contextRunner
                    .withPropertyValues("event-relay.jpa.enabled=false")
                    .run(context -> {
                        assertThat(context)
                                .hasSingleBean(EventSender.class)
                                .getBean(EventSender.class)
                                .isInstanceOf(KafkaEventSender.class);
                    });
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TestSupport {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider() {
            return new FixedCurrentApplicationNamespaceProvider("test");
        }

        @Bean
        EntityManager entityManager() {
            return mock(EntityManager.class);
        }

        @Bean
        JpaStoredEventPersistencePort jpaStoredEventPersistencePort() {
            return mock(JpaStoredEventPersistencePort.class);
        }

        @Bean
        KafkaTemplate<String ,String> kafkaTemplate() {
            return mock(KafkaTemplate.class);
        }

        @Bean
        KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
            return new KafkaListenerEndpointRegistry();
        }

        @Bean
        ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory() {
            return new ConcurrentKafkaListenerContainerFactory<>();
        }
    }
}
