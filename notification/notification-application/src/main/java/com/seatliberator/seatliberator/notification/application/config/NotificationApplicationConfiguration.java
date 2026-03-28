package com.seatliberator.seatliberator.notification.application.config;

import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.notification.api.NotificationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class NotificationApplicationConfiguration {

    @Bean
    FixedCurrentApplicationNamespaceProvider fixedCurrentApplicationNamespaceProvider() {
        return new FixedCurrentApplicationNamespaceProvider(NotificationApi.NAMESPACE);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
