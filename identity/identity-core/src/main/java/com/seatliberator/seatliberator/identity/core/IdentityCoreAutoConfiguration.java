package com.seatliberator.seatliberator.identity.core;

import com.seatliberator.seatliberator.identity.core.introspection.IntrospectionFactory;
import com.seatliberator.seatliberator.identity.core.introspection.SimpleIntrospectionFactory;
import com.seatliberator.seatliberator.identity.core.role.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(IdentityRoleConfigurationProperties.class)
public class IdentityCoreAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(IntrospectionFactory.class)
    SimpleIntrospectionFactory simpleIntrospectionFactory() {
        return new SimpleIntrospectionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(NamespaceRoleFormatter.class)
    SeparatorNamespaceRoleFormatter separatorNamespaceRoleFormatter(
            IdentityRoleConfigurationProperties properties
    ) {
        return new SeparatorNamespaceRoleFormatter(properties.separator());
    }

    @Bean
    @ConditionalOnMissingBean(NamespaceRoleDeserializer.class)
    SeparatorNamespaceRoleDeserializer separatorNamespaceRoleDeserializer(
            IdentityRoleConfigurationProperties properties
    ) {
        return new SeparatorNamespaceRoleDeserializer(properties.separator());
    }
}
