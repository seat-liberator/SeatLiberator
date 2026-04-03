package com.seatliberator.seatliberator.eventrelay.autoconfigure.provider;

import com.seatliberator.seatliberator.eventrelay.core.provider.ProducerProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.core.env.Environment;

public class SpringApplicationNameProducerProvider implements ProducerProvider {
    private static final String LOCATION = "spring.application.name";
    private static final String DEFAULT_UNKNOWN_ALIAS = "unknown-service";
    private final Environment environment;

    public SpringApplicationNameProducerProvider(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public @NonNull String get() {
        var name = environment.getProperty(LOCATION);
        return (name == null || name.isBlank()) ? DEFAULT_UNKNOWN_ALIAS : name;
    }
}
