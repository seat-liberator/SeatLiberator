package com.seatliberator.seatliberator.eventrelay.support.kafka;

import com.seatliberator.seatliberator.eventrelay.core.definition.RegisteredEventDefinition;
import org.jspecify.annotations.NonNull;

public class NamespaceTopicFactory implements TopicFactory {
    private final String subscribePrefix;
    private final String topicDelimiter;

    public NamespaceTopicFactory(
            @NonNull String subscribePrefix,
            @NonNull String topicDelimiter
    ) {
        if (subscribePrefix.isBlank()) {
            throw new IllegalArgumentException("topicSuffix must not be blank.");
        }
        if (topicDelimiter.isBlank()) {
            throw new IllegalArgumentException("topicDelimiter must not be blank.");
        }
        this.subscribePrefix = subscribePrefix;
        this.topicDelimiter = topicDelimiter;
    }


    @Override
    public @NonNull String fromRegistration(@NonNull RegisteredEventDefinition registeredEventDefinition) {
        return String.join(topicDelimiter, registeredEventDefinition.namespace().value(), subscribePrefix);
    }
}
