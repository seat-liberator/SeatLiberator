package com.seatliberator.seatliberator.identity.core.role;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "identity.role")
public record IdentityRoleConfigurationProperties(
        @NotBlank String separator
) {
    public IdentityRoleConfigurationProperties(@DefaultValue(":") String separator) {
        this.separator = separator;
    }
}
