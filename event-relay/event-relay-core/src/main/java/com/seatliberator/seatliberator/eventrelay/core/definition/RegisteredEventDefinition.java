package com.seatliberator.seatliberator.eventrelay.core.definition;

import org.jspecify.annotations.NonNull;

public record RegisteredEventDefinition(
        @NonNull EventDefinitionNamespace namespace,
        @NonNull EventDefinition<?> definition
) {
}
