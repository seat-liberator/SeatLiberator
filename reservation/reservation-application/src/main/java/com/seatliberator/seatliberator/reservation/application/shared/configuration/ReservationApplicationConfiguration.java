package com.seatliberator.seatliberator.reservation.application.shared.configuration;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.reservation.api.ReservationApi;
import com.seatliberator.seatliberator.reservation.application.shared.notifier.Notifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;

@Configuration
public class ReservationApplicationConfiguration {
    @Bean
    FixedCurrentApplicationNamespaceProvider fixedCurrentApplicationNamespaceProvider() {
        return new FixedCurrentApplicationNamespaceProvider(ReservationApi.NAMESPACE);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    Notifier notifier(
            EventTraceHolder eventTraceHolder,
            EventPublisher eventPublisher,
            ObjectMapper objectMapper
    ) {
        return new Notifier(eventTraceHolder, eventPublisher, objectMapper);
    }
}
