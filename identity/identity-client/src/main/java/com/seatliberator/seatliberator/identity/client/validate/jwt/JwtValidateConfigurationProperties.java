package com.seatliberator.seatliberator.identity.client.validate.jwt;

import com.seatliberator.seatliberator.identity.client.properties.ServerProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "identity.validate.jwt")
public record JwtValidateConfigurationProperties(
        boolean enabled,
        @Valid ServerProperties jwksServer
) {
    public JwtValidateConfigurationProperties {
        jwksServer = jwksServer != null ? jwksServer : ServerProperties.getDefault();
    }

    @AssertTrue(message = "When identity.validate.jwt.enabled=true, jwks-server.base-url and jwks-server.uri must both be set")
    public boolean isValidWhenEnabled() {
        if (!enabled) return true;
        return jwksServer != null && jwksServer().isAllConfigured();
    }
}
