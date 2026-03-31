package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.TestFixture.fixedClock;

@TestConfiguration
public class ReservationIntegrationTestConfiguration {

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
                .header("alg", "none")
                .subject("test-user")
                .claim("scopes", List.of())
                .build();
    }

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
