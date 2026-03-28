package com.seatliberator.seatliberator.notification.api.event;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionCollection;
import com.seatliberator.seatliberator.eventrelay.core.definition.SimpleEventDefinitionCollection;
import com.seatliberator.seatliberator.notification.api.NotificationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NotificationEventDefinitionConfiguration {
    @Bean
    EventDefinitionCollection notificationEventDefinitionCollection(
            NotificationCreateRequestEventDefinition notificationCreateRequestEventDefinition
    ) {
        return new SimpleEventDefinitionCollection(
                NotificationApi.NAMESPACE,
                List.of(notificationCreateRequestEventDefinition)
        );
    }
}
