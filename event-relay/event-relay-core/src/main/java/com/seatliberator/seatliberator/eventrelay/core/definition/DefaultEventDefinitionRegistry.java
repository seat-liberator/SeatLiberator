package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultEventDefinitionRegistry implements EventDefinitionRegistry, EventDefinitionRegistrar {
    private final Map<String, EventDefinition<?>> registry;

    public DefaultEventDefinitionRegistry(List<EventDefinition<?>> definitions) {
        this.registry = definitions.stream().collect(
                Collectors.toMap(
                        this::keyOf,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException("Duplicated event definition: " + keyOf(a));
                        },
                        LinkedHashMap::new
                )
        );
    }

    @Override
    public @Nullable EventDefinition<?> resolve(EventType type) {
        return registry.get(type.name());
    }

    @Override
    public @NonNull List<EventDefinition<?>> listAll() {
        return List.copyOf(registry.values());
    }

    @Override
    public void register(@NonNull EventDefinition<?> definition) {
        var previous = registry.putIfAbsent(keyOf(definition), definition);
        if (previous != null) {
            throw new IllegalStateException("Duplicated event definition: " + keyOf(definition));
        }
    }

    private String keyOf(EventDefinition<?> definition) {
        return keyOf(definition.type());
    }

    private String keyOf(EventType type) {
        return type.name();
    }
}
