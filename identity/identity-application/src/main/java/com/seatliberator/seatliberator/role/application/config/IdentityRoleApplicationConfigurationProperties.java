package com.seatliberator.seatliberator.role.application.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "identity.role")
public record IdentityRoleApplicationConfigurationProperties(
        @NotBlank String separator
) {
    public IdentityRoleApplicationConfigurationProperties(@DefaultValue(":") String separator) {
        this.separator = separator;
    }
}
