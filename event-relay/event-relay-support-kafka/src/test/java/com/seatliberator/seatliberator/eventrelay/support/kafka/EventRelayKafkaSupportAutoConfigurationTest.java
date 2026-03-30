package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.EventRelayCoreAutoConfiguration;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventRelayKafkaSupportAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    KafkaAutoConfiguration.class,
                    EventRelayCoreAutoConfiguration.class,
                    EventRelayKafkaSupportAutoConfiguration.class
            ))
            .withPropertyValues("spring.application.name=test-app")
            .withUserConfiguration(TestConfiguration.class);

    @Test
    @DisplayName("Kafka listener 인프라가 자동 구성되면 KafkaDynamicEventSubscriber를 등록한다")
    void registersKafkaDynamicEventSubscriber() {
        contextRunner.run(context ->
                assertThat(context.getBeanNamesForType(KafkaDynamicEventSubscriber.class)).hasSize(1)
        );
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfiguration {

        @Bean
        EventStore eventStore() {
            return new NoOpEventStore();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    static class NoOpEventStore implements EventStore {

        @Override
        public void accept(EventEnvelope envelope, EventFlow flow, Instant acceptedAt) {
        }

        @Override
        public List<EventEnvelope> claimBatch(EventFlow flow, Instant claimedAt) {
            return List.of();
        }

        @Override
        public void reportCompleted(String eventId, Instant resolvedAt) {
        }

        @Override
        public void reportFailed(String eventId, Instant resolvedAt) {
        }
    }
}
