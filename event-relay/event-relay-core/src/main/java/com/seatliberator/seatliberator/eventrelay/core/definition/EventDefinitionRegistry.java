package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface EventDefinitionRegistry {
    @Nullable EventDefinitionCollection resolve(@NonNull ApplicationNamespace namespace);

    @Nullable RegisteredEventDefinition resolve(@NonNull EventType type);

    @NonNull List<EventDefinitionCollection> listAll();
}
