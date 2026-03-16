package com.seatliberator.seatliberator.identity.client.validate.introspection.web;

import com.seatliberator.seatliberator.identity.client.properties.ServerProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "identity.validate.introspection.web")
public record WebClientIntrospectionConfigurationProperties(
        boolean enabled,
        @Valid ServerProperties server
) {
    public WebClientIntrospectionConfigurationProperties {
        server = server != null ? server : ServerProperties.getDefault();
    }

    @AssertTrue(message = "When identity.validate.introspection.web.enabled=true, server.base-url and server.uri must both be set")
    public boolean isValidWhenEnabled() {
        if (!enabled) return true;
        return server != null && server().isAllConfigured();
    }
}

