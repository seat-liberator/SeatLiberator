package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface EventDefinitionRegistry {
    @Nullable EventDefinition<?> resolve(EventType type);

    @NonNull List<EventDefinition<?>> listAll();
}
