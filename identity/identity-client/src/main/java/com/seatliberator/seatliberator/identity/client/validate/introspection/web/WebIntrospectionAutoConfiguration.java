package com.seatliberator.seatliberator.identity.client.validate.introspection.web;

import com.seatliberator.seatliberator.identity.core.introspect.Introspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@ConditionalOnBean(WebClient.class)
@ConditionalOnProperty(
        prefix = "identity.validate.introspection.web",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(WebClientIntrospectionConfigurationProperties.class)
public class WebIntrospectionAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(WebIntrospectionAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(WebClient.class)
    WebClient webClient(
            WebClientIntrospectionConfigurationProperties properties
    ) {
        var baseUrl = properties.server().baseUrl();

        log.debug("WebClient configured with baseUrl={}", baseUrl);

        return WebClient.builder()
                .baseUrl(baseUrl.toString())
                .build();
    }

    @Bean
    @ConditionalOnClass(WebClient.class)
    Introspector webClientIntrospector(
            WebClient webClient,
            WebClientIntrospectionConfigurationProperties properties
    ) {
        var uri = properties.server().uri();

        log.debug("WebClientIntrospector configured with uri={}", uri);

        return new WebClientIntrospector(
                webClient,
                uri.toString()
        );
    }
}
