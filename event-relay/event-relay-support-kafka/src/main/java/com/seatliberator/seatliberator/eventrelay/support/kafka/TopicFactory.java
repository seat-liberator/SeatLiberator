package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.definition.RegisteredEventDefinition;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface TopicFactory {
    @NonNull String fromRegistration(@NonNull RegisteredEventDefinition registeredEventDefinition);
}
