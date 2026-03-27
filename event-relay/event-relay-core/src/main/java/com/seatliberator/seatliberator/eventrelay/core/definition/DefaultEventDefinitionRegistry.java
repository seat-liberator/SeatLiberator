package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultEventDefinitionRegistry implements EventDefinitionRegistry, EventDefinitionRegistrar {
    private final Map<EventDefinitionNamespace, EventDefinitionCollection> collections;
    private final Map<String, RegisteredEventDefinition> definitionsByType;

    public DefaultEventDefinitionRegistry() {
        this.collections = new LinkedHashMap<>();
        this.definitionsByType = new LinkedHashMap<>();
    }

    public DefaultEventDefinitionRegistry(@NonNull List<EventDefinitionCollection> collections) {
        this();

        for (var collection : collections) {
            register(collection);
        }
    }

    @Override
    public void register(@NonNull EventDefinitionCollection collection) {
        var namespace = collection.namespace();

        var previousCollection = collections.putIfAbsent(namespace, collection);
        if (previousCollection != null) {
            throw new IllegalStateException(String.format(
                    "EventDefinitionCollection already registered for namespace: %s",
                    namespace
            ));
        }

        for (var definition : collection.definitions()) {
            var type = definition.type();
            var registration = new RegisteredEventDefinition(namespace, definition);
            var previousType = definitionsByType.putIfAbsent(type.name(), registration);
            if (previousType != null) {
                throw new IllegalStateException(String.format(
                        "Duplicate event definition type: %s",
                        type.name()
                ));
            }
        }
    }

    @Override
    public @Nullable EventDefinitionCollection resolve(@NonNull EventDefinitionNamespace namespace) {
        return collections.get(namespace);
    }

    @Override
    public @Nullable RegisteredEventDefinition resolve(@NonNull EventType type) {
        return definitionsByType.get(type.name());
    }

    @Override
    public @NonNull List<EventDefinitionCollection> listAll() {
        return List.copyOf(collections.values());
    }
}
