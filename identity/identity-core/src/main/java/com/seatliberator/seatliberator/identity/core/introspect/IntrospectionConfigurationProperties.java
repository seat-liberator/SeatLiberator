package com.seatliberator.seatliberator.identity.core.introspect;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "identity.introspection")
public record IntrospectionConfigurationProperties(
        @DefaultValue("900000") Long expirationMs
) {
}
