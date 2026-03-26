package com.seatliberator.seatliberator.eventrelay.core.definition;

import org.jspecify.annotations.NonNull;

import java.util.List;

public record SimpleEventDefinitionCollection(
        @NonNull EventDefinitionNamespace namespace,
        @NonNull List<EventDefinition<?>> definitions
) implements EventDefinitionCollection {
    public SimpleEventDefinitionCollection {
        definitions = List.copyOf(definitions);
    }
}
