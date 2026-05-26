package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.config;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationFederatedUseCase;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler.FederatedAuthenticationFailureHandler;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler.FederatedAuthenticationSuccessHandler;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.service.CustomOAuth2UserService;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.service.CustomOidcUserService;
import com.seatliberator.seatliberator.identity.server.security.shared.response.ResponseWriter;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenResponseProcessor;
import com.seatliberator.seatliberator.starter.security.customizer.HttpSecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class FederatedAuthenticationConfiguration {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    HttpSecurityCustomizer federatedHttpSecurityCustomizer(
            CustomOidcUserService customOidcUserService,
            CustomOAuth2UserService customOAuth2UserService,
            FederatedAuthenticationSuccessHandler successHandler,
            FederatedAuthenticationFailureHandler failureHandler
    ) {
        return httpSecurity -> httpSecurity
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/oauth2/**", "/login/**")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(config -> config
                                .oidcUserService(customOidcUserService)
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );
    }

    @Bean
    CustomOAuth2UserService customOAuth2UserService(FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry) {
        return new CustomOAuth2UserService(federatedPrincipalMapperRegistry);
    }

    @Bean
    CustomOidcUserService customOidcUserService(FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry) {
        return new CustomOidcUserService(federatedPrincipalMapperRegistry);
    }

    @Bean
    FederatedAuthenticationSuccessHandler federatedAuthenticationSuccessHandler(
            AuthenticationFederatedUseCase useCase,
            TokenResponseProcessor tokenResponseProcessor
    ) {
        return new FederatedAuthenticationSuccessHandler(
                useCase,
                tokenResponseProcessor
        );
    }

    @Bean
    FederatedAuthenticationFailureHandler federatedAuthenticationFailureHandler(ResponseWriter responseWriter) {
        return new FederatedAuthenticationFailureHandler(responseWriter);
    }
}
