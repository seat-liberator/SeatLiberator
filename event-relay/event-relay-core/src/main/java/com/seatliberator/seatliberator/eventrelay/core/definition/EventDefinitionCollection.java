package com.seatliberator.seatliberator.eventrelay.core.definition;

import org.jspecify.annotations.NonNull;

import java.util.List;

public interface EventDefinitionCollection {
    @NonNull EventDefinitionNamespace namespace();

    @NonNull List<EventDefinition<?>> definitions();
}
