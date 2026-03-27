package com.seatliberator.seatliberator.role.application.config;

import com.seatliberator.seatliberator.role.application.formatter.SeparatorNamespaceRoleFormatter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdentityRoleApplicationConfigurationProperties.class)
public class RoleApplicationConfiguration {

    @Bean
    SeparatorNamespaceRoleFormatter separatorNamespaceRoleFormatter(
            IdentityRoleApplicationConfigurationProperties properties
    ) {
        return new SeparatorNamespaceRoleFormatter(properties.separator());
    }
}
