package com.seatliberator.seatliberator.bootstrap.application;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "seatliberator.application",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock clock(ApplicationProperties properties) {
        return Clock.system(properties.timezone());
    }
}
