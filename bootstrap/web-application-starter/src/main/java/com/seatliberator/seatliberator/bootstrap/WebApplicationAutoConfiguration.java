package com.seatliberator.seatliberator.bootstrap;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "seatliberator.application",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(WebApplicationProperties.class)
public class WebApplicationAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock clock(WebApplicationProperties properties) {
        return Clock.system(ZoneId.of(properties.zoneId()));
    }
}