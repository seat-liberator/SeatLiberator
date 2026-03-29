package com.seatliberator.seatliberator.eventrelay.core.definition;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import org.jspecify.annotations.NonNull;

public record RegisteredEventDefinition(
        @NonNull ApplicationNamespace namespace,
        @NonNull EventDefinition<?> definition
) {
}
