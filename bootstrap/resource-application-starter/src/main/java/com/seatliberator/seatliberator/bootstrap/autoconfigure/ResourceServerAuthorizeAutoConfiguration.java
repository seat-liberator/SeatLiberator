package com.seatliberator.seatliberator.bootstrap.autoconfigure;

import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerAuthorizeRequestMatcherCustomizer;
import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerHttpSecurityCustomizer;
import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerOAuth2Customizer;
import com.seatliberator.seatliberator.identity.client.web.ActorContextBindingFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "seatliberator.resource-server.security.authorize",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ResourceServerAuthorizeProperties.class)
public class ResourceServerAuthorizeAutoConfiguration {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    ResourceServerOAuth2Customizer defaultResourceServerOAuth2Customizer(
            JwtDecoder jwtDecoder,
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationTokenConverter
    ) {
        return oauth -> oauth.jwt(jwt -> jwt
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationTokenConverter)
        );
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    ResourceServerAuthorizeRequestMatcherCustomizer defaultResourceServerAuthorizeRequestMatcherCustomizer(
            ResourceServerAuthorizeProperties properties
    ) {
        return auth -> {
            for (var permit : properties.permits()) auth.requestMatchers(permit).permitAll();
        };
    }

    @Bean
    ResourceServerHttpSecurityCustomizer oauth2SecurityCustomizer(
            ActorContextBindingFilter actorContextBindingFilter,
            ObjectProvider<List<ResourceServerOAuth2Customizer>> oauth2CustomizerProvider
    ) {
        return http -> http
                .oauth2ResourceServer(oauth2 -> {
                    var customizers = new ArrayList<>(oauth2CustomizerProvider.getIfAvailable(ArrayList::new));
                    AnnotationAwareOrderComparator.sort(customizers);
                    for (var customizer : customizers) customizer.customize(oauth2);
                })
                .addFilterAfter(actorContextBindingFilter, BearerTokenAuthenticationFilter.class);
    }

    @Bean
    ResourceServerHttpSecurityCustomizer authorizeHttpRequestSecurityCustomizer(
            ObjectProvider<List<ResourceServerAuthorizeRequestMatcherCustomizer>> authorizeCustomizerProvider,
            ResourceServerAuthorizeProperties authorizeProperties
    ) {
        return http -> http
                .authorizeHttpRequests(auth -> {
                    var customizers = new ArrayList<>(authorizeCustomizerProvider.getIfAvailable(ArrayList::new));
                    AnnotationAwareOrderComparator.sort(customizers);
                    for (var customizer : customizers) customizer.customize(auth);

                    switch (authorizeProperties.requireAuthorizeAny()) {
                        case ALL -> auth.anyRequest().authenticated();
                        case NEVER -> auth.anyRequest().permitAll();
                    }
                });
    }
}
