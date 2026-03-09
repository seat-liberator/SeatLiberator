package com.seatliberator.seatliberator.jwks.application.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JWKSProperties.class)
public class JWKSConfiguration {

}
