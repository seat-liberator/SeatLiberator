package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record SimpleEventDefinitionCollection(
        @NonNull ApplicationNamespace namespace,
        @NonNull List<EventDefinition<?>> definitions
) implements EventDefinitionCollection {
    public SimpleEventDefinitionCollection {
        definitions = List.copyOf(definitions);
    }
}
