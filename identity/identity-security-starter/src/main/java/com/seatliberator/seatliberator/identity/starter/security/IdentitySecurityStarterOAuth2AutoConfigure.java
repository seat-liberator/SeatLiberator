package com.seatliberator.seatliberator.identity.starter.security;

import com.seatliberator.seatliberator.bootstrap.security.SecurityStarterAutoConfigure;
import com.seatliberator.seatliberator.bootstrap.security.customizer.HttpSecurityCustomizer;
import com.seatliberator.seatliberator.identity.starter.security.jwt.ActorContextBindingFilter;
import com.seatliberator.seatliberator.identity.starter.security.jwt.ActorContextJwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

@AutoConfiguration(after = SecurityStarterAutoConfigure.class)
@ConditionalOnProperty(
        prefix = "identity.starter.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class IdentitySecurityStarterOAuth2AutoConfigure {

    @Bean("oAuth2ResourceServerCustomizer")
    @ConditionalOnBean({
            JwtDecoder.class,
            ActorContextJwtAuthenticationConverter.class,
            ActorContextBindingFilter.class
    })
    @ConditionalOnMissingBean(name = "oAuth2ResourceServerCustomizer")
    HttpSecurityCustomizer oAuth2ResourceServerCustomizer(
            JwtDecoder jwtDecoder,
            ActorContextJwtAuthenticationConverter converter,
            ActorContextBindingFilter filter
    ) {
        return httpSecurity -> {
            httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                            .decoder(jwtDecoder)
                            .jwtAuthenticationConverter(converter)
                    ))
                    .addFilterAfter(filter, BearerTokenAuthenticationFilter.class);
        };
    }
}
