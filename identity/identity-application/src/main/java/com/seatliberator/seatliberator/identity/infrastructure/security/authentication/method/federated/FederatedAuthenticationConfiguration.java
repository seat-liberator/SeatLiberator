package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated;

import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.AccountExistenceChecker;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.infrastructure.security.FilterChainUtils;
import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler.DefaultFederatedSignInProcessor;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler.FederatedAuthenticationFailureHandler;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler.FederatedAuthenticationSuccessHandler;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler.FederatedSignInProcessor;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper.*;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.service.CustomOAuth2UserService;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.service.CustomOidcUserService;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler.TokenIssueProcessor;
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
        return new DefaultFederatedPrincipalMapperRegistry(federatedPrincipalMappers);
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
    FederatedSignInProcessor federatedSignInProcessor(
            AccountAuthenticator accountAuthenticator,
            AccountExistenceChecker accountExistenceChecker,
            UserRegistrar userRegistrar
    ) {
        return new DefaultFederatedSignInProcessor(
                accountAuthenticator,
                accountExistenceChecker,
                userRegistrar
        );
    }

    @Bean
    AuthenticationSuccessHandler federatedAuthenticationSuccessHandler(
            FederatedSignInProcessor federatedSignInProcessor,
            TokenIssueProcessor tokenIssueProcessor,
            ResponseWriter responseWriter
    ) {
        return new FederatedAuthenticationSuccessHandler(
                federatedSignInProcessor,
                tokenIssueProcessor,
                responseWriter
        );
    }

    @Bean
    AuthenticationFailureHandler federatedAuthenticationFailureHandler(
            ResponseWriter responseWriter
    ) {
        return new FederatedAuthenticationFailureHandler(responseWriter);
    }

    @Bean
    @Order(1)
    SecurityFilterChain federatedAuthenticationSecurityFilterChain(
            HttpSecurity httpSecurity,
            CorsConfigurationSource corsConfigurationSource,
            OidcUserService customOidcUserService,
            AuthenticationSuccessHandler federatedAuthenticationSuccessHandler,
            AuthenticationFailureHandler federatedAuthenticationFailureHandler
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
                                )
                                .successHandler(federatedAuthenticationSuccessHandler)
                                .failureHandler(federatedAuthenticationFailureHandler)
                );

        return httpSecurity.build();
    }
}
