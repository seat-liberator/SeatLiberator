package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.config;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationFederatedUseCase;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler.FederatedAuthenticationFailureHandler;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler.FederatedAuthenticationSuccessHandler;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapper;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.GithubOAuth2PrincipalMapper;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.GoogleOidcPrincipalMapper;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.service.CustomOAuth2UserService;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.service.CustomOidcUserService;
import com.seatliberator.seatliberator.identity.server.security.shared.config.FilterChainUtils;
import com.seatliberator.seatliberator.identity.server.security.shared.response.ResponseWriter;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenResponseProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class FederatedAuthenticationConfiguration {
    @Bean
    FederatedPrincipalMapper githubOAuth2PrincipalMapper() {
        return new GithubOAuth2PrincipalMapper();
    }

    @Bean
    FederatedPrincipalMapper googleOidcPrincipalMapper() {
        return new GoogleOidcPrincipalMapper();
    }

    @Bean
    FederatedPrincipalMapperRegistry oidcProfileMapperRegistry(
            List<FederatedPrincipalMapper> federatedPrincipalMappers
    ) {
        return new FederatedPrincipalMapperRegistry(federatedPrincipalMappers);
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService(
            FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry
    ) {
        return new CustomOAuth2UserService(federatedPrincipalMapperRegistry);
    }

    @Bean
    OidcUserService customOidcUserService(
            FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry
    ) {
        return new CustomOidcUserService(federatedPrincipalMapperRegistry);
    }

    @Bean
    @Qualifier("federated")
    AuthenticationSuccessHandler federatedAuthenticationSuccessHandler(
            AuthenticationFederatedUseCase useCase,
            TokenResponseProcessor tokenResponseProcessor
    ) {
        return new FederatedAuthenticationSuccessHandler(
                useCase,
                tokenResponseProcessor
        );
    }

    @Bean
    @Qualifier("federated")
    AuthenticationFailureHandler federatedAuthenticationFailureHandler(
            ResponseWriter responseWriter
    ) {
        return new FederatedAuthenticationFailureHandler(responseWriter);
    }

    @Bean
    @Order(1)
    SecurityFilterChain federatedAuthenticationSecurityFilterChain(
            HttpSecurity httpSecurity,
            @Qualifier("custom") CorsConfigurationSource corsConfigurationSource,
            OidcUserService customOidcUserService,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService,
            @Qualifier("federated") AuthenticationSuccessHandler federatedAuthenticationSuccessHandler,
            @Qualifier("federated") AuthenticationFailureHandler federatedAuthenticationFailureHandler
    ) {
        FilterChainUtils.configureDefault(httpSecurity, corsConfigurationSource);

        httpSecurity
                .securityMatcher("/oauth2/**", "/login/**")
                .authorizeHttpRequests(
                        auth -> auth
                                .anyRequest().permitAll()
                )
                .oauth2Login(
                        oauth -> oauth
                                .userInfoEndpoint(

                                        u -> u
                                                .oidcUserService(customOidcUserService)
                                                .userService(customOAuth2UserService)
                                )
                                .successHandler(federatedAuthenticationSuccessHandler)
                                .failureHandler(federatedAuthenticationFailureHandler)
                );

        return httpSecurity.build();
    }
}
