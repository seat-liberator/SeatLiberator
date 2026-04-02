package com.seatliberator.seatliberator.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seatliberator.bootstrap.application")
public record WebApplicationConfigurationProperties(

) {
}
