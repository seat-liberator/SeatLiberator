package com.seatliberator.seatliberator.identity.core.introspect;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity.introspection")
public record IntrospectionConfigurationProperties(
        Long expirationMs
) {
}
