package com.seatliberator.seatliberator.identity.client.validate.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@AutoConfiguration
@ConditionalOnClass({
        JwtDecoder.class,
        NimbusJwtDecoder.class
})
@ConditionalOnProperty(
        prefix = "identity.validate.jwt",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(JwtValidateConfigurationProperties.class)
public class JwtValidateAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(JwtValidateAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(
            JwtValidateConfigurationProperties properties
    ) {
        var baseUrl = properties.jwksServer().baseUrl();
        var uri = properties.jwksServer().uri();
        var jwksUrl = baseUrl.resolve(uri);

        log.debug("JwtDecoder configured with jwk-set-uri={}", jwksUrl);

        return NimbusJwtDecoder.withJwkSetUri(jwksUrl.toString()).build();
    }
}
