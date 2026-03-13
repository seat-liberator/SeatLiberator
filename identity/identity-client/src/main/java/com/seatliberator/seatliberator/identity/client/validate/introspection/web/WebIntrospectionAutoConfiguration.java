package com.seatliberator.seatliberator.identity.client.validate.introspection.web;

import com.seatliberator.seatliberator.identity.core.IdentityAutoConfiguration;
import com.seatliberator.seatliberator.identity.core.introspect.Introspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration(before = IdentityAutoConfiguration.class)
@ConditionalOnClass(WebClient.class)
@ConditionalOnProperty(
        prefix = "identity.validate.introspection.web",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(WebClientIntrospectionConfigurationProperties.class)
public class WebIntrospectionAutoConfiguration {
    static final String IDENTITY_INTROSPECTION_WEB_CLIENT = "identityIntrospectionWebClient";
    private static final Logger log = LoggerFactory.getLogger(WebIntrospectionAutoConfiguration.class);

    @Bean(name = IDENTITY_INTROSPECTION_WEB_CLIENT)
    @ConditionalOnMissingBean(name = IDENTITY_INTROSPECTION_WEB_CLIENT)
    WebClient webClient(
            WebClient.Builder builder,
            WebClientIntrospectionConfigurationProperties properties
    ) {
        var baseUrl = properties.server().baseUrl();

        log.debug("WebClient configured with baseUrl={}", baseUrl);

        return builder
                .baseUrl(baseUrl.toString())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(Introspector.class)
    Introspector webClientIntrospector(
            @Qualifier(IDENTITY_INTROSPECTION_WEB_CLIENT) WebClient webClient,
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
