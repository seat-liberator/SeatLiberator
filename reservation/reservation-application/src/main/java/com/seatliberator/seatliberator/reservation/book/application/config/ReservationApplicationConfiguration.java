package com.seatliberator.seatliberator.reservation.book.application.config;

import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.reservation.api.ReservationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
