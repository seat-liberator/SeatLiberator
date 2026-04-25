package com.seatliberator.seatliberator.reservation.persistence.integration;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.factory.ThreadLocalEventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;

@TestConfiguration
public class ReservationPersistenceIntegrationTestConfiguration {

    @Bean
    @Primary
    Clock testClock() {
        return fixedClock;
    }

    @Bean
    TestEventPublisher testEventPublisher() {
        return new TestEventPublisher();
    }

    @Bean
    @Primary
    EventPublisher capturingEventPublisher(TestEventPublisher testEventPublisher) {
        return testEventPublisher;
    }

    @Bean
    EventTraceHolder testEventTraceHolder() {
        return new ThreadLocalEventTraceHolder();
    }

    public static class TestEventPublisher implements EventPublisher {
        private final List<PublishedEvent> publishedEvents = new ArrayList<>();

        @Override
        public synchronized <P extends EventPayload> void publish(EventType type, P payload) {
            publishedEvents.add(new PublishedEvent(type, payload));
        }

        public synchronized List<PublishedEvent> publishedEvents() {
            return List.copyOf(publishedEvents);
        }

        public synchronized void clear() {
            publishedEvents.clear();
        }
    }

    public record PublishedEvent(
            EventType type,
            EventPayload payload
    ) {
    }

}
