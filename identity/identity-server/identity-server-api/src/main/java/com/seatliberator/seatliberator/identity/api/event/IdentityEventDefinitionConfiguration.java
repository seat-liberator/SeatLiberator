package com.seatliberator.seatliberator.identity.api.event;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionCollection;
import com.seatliberator.seatliberator.eventrelay.core.definition.SimpleEventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.definition.SimpleEventDefinitionCollection;
import com.seatliberator.seatliberator.identity.api.IdentityApi;
import com.seatliberator.seatliberator.identity.api.event.payload.UserRegisteredEventPayload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class IdentityEventDefinitionConfiguration {
    @Bean
    EventDefinitionCollection identityEventDefinitionCollection() {
        return new SimpleEventDefinitionCollection(
                IdentityApi.NAMESPACE,
                List.of(
                        new SimpleEventDefinition<>(IdentityEventType.USER_REGISTERED, UserRegisteredEventPayload.class)
                )
        );
    }
}
