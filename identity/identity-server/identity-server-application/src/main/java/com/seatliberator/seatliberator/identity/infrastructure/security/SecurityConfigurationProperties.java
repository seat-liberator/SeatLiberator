package com.seatliberator.seatliberator.identity.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "identity.application.security")
public record SecurityConfigurationProperties(
        List<String> allowedOrigins,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        Boolean allowCredentials,
        List<String> exposedHeaders
) {
    public SecurityConfigurationProperties {
        allowedOrigins = allowedOrigins != null ? allowedOrigins : new ArrayList<>();
        allowedMethods = allowedMethods != null ? allowedMethods : new ArrayList<>();
        allowedHeaders = allowedHeaders != null ? allowedHeaders : new ArrayList<>();
        allowCredentials = allowCredentials != null ? allowCredentials : true;
        exposedHeaders = exposedHeaders != null ? exposedHeaders : new ArrayList<>();
    }
}
