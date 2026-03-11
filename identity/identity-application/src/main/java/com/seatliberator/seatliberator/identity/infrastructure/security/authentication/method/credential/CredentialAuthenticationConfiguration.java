package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential;

import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.FilterChainUtils;
import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.filter.JsonCredentialSignInProcessingFilter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.filter.JsonCredentialSignUpProcessingFilter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.handler.CredentialAuthenticationFailureHandler;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.handler.CredentialAuthenticationSuccessHandler;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.provider.CredentialSignInProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.provider.CredentialSignUpProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler.TokenIssueProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CredentialAuthenticationConfigurationProperties.class)
public class CredentialAuthenticationConfiguration {
    @Bean
    JsonCredentialSignUpProcessingFilter jsonCredentialRegisterProcessingFilter(
            AuthenticationManager credentialAuthenticationManager,
            ObjectMapper objectMapper,
            CredentialAuthenticationConfigurationProperties properties
    ) {
        var endpoint = properties.signUp();
        var matcher = PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.valueOf(endpoint.method()), endpoint.uri());

        var filter = new JsonCredentialSignUpProcessingFilter(matcher, objectMapper);

        filter.setAuthenticationManager(credentialAuthenticationManager);

        return filter;
    }

    @Bean
    AuthenticationManager credentialAuthenticationManager(
            CredentialSignUpProvider credentialSignUpProvider,
            CredentialSignInProvider credentialSignInProvider
    ) {
        return new ProviderManager(List.of(
                credentialSignInProvider,
                credentialSignUpProvider
        ));
    }

    @Bean
    JsonCredentialSignInProcessingFilter jsonCredentialSignInProcessingFilter(
            AuthenticationManager credentialAuthenticationManager,
            ObjectMapper objectMapper,
            CredentialAuthenticationConfigurationProperties properties
    ) {
        var endpoint = properties.signUp();
        var matcher = PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.valueOf(endpoint.method()), endpoint.uri());

        var filter = new JsonCredentialSignInProcessingFilter(matcher, objectMapper);

        filter.setAuthenticationManager(credentialAuthenticationManager);

        return filter;
    }

    @Bean
    CredentialSignInProvider credentialSignInProvider(
            AccountAuthenticator accountAuthenticator
    ) {
        return new CredentialSignInProvider(accountAuthenticator);
    }

    @Bean
    CredentialSignUpProvider credentialSignUpProvider(
            UserRegistrar userRegistrar,
            TokenProvider jwtProvider
    ) {
        return new CredentialSignUpProvider(
                userRegistrar,
                jwtProvider
        );
    }

    @Bean
    AuthenticationSuccessHandler credentialAuthenticationSuccessHandler(
            TokenIssueProcessor tokenIssueProcessor,
            ResponseWriter responseWriter
    ) {
        return new CredentialAuthenticationSuccessHandler(
                tokenIssueProcessor,
                responseWriter
        );
    }

    @Bean
    AuthenticationFailureHandler credentialAuthenticationFailureHandler(
            ResponseWriter responseWriter
    ) {
        return new CredentialAuthenticationFailureHandler(responseWriter);
    }

    @Bean
    @Order(2)
    SecurityFilterChain credentialAuthenticationSecurityFilterChain(
            HttpSecurity httpSecurity,
            CorsConfigurationSource corsConfigurationSource,
            JsonCredentialSignInProcessingFilter jsonCredentialSignInProcessingFilter,
            JsonCredentialSignUpProcessingFilter jsonCredentialSignUpProcessingFilter
    ) {
        FilterChainUtils.configureDefault(httpSecurity, corsConfigurationSource);

        httpSecurity
                .securityMatcher("/auth/**")
                .authorizeHttpRequests(
                        auth -> auth
                                .anyRequest().permitAll()
                )
                .addFilterBefore(jsonCredentialSignInProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jsonCredentialSignUpProcessingFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
