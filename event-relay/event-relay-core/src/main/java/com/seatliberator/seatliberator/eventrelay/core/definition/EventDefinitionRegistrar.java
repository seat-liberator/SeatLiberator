package com.seatliberator.seatliberator.eventrelay.core.definition;

import org.jspecify.annotations.NonNull;

public interface EventDefinitionRegistrar {
    void register(@NonNull EventDefinition<?> definition);
}
