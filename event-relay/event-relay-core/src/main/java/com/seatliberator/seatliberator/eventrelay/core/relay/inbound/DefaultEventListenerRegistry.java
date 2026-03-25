package com.seatliberator.seatliberator.eventrelay.core.relay.inbound;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultEventListenerRegistry implements EventListenerRegistry, EventListenerRegistrar {
    private final Map<String, EventListener<?>> registry;

    public DefaultEventListenerRegistry(List<EventListener<?>> listeners) {
        this.registry = listeners.stream().collect(
                Collectors.toMap(
                        this::keyOf,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException("Duplicated event listener: " + keyOf(a));
                        },
                        LinkedHashMap::new
                )
        );
    }

    @Override
    public @Nullable EventListener<?> resolve(EventType type) {
        return registry.get(keyOf(type));
    }

    @Override
    public @NonNull List<EventListener<?>> listAll() {
        return List.copyOf(registry.values());
    }

    @Override
    public void register(@NonNull EventListener<?> listener) {
        var previous = registry.putIfAbsent(keyOf(listener), listener);
        if (previous != null) {
            throw new IllegalStateException("Duplicated event listener: " + keyOf(listener));
        }
    }

    private String keyOf(EventListener<?> listener) {
        return keyOf(listener.definition().type());
    }

    private String keyOf(EventType type) {
        return type.name();
    }
}
