package com.seatliberator.seatliberator.reservation.application.config;

import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.reservation.api.ReservationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationApplicationConfiguration {

    @Bean
    FixedCurrentApplicationNamespaceProvider fixedCurrentApplicationNamespaceProvider() {
        return new FixedCurrentApplicationNamespaceProvider(ReservationApi.NAMESPACE);
    }
}
