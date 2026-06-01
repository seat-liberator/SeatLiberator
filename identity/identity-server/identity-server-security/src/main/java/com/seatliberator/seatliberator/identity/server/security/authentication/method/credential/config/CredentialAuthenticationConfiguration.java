package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.config;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationCredentialUseCase;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.RegisterCredentialAccountUseCase;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter.JsonCredentialSignInProcessingFilter;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter.JsonCredentialSignUpProcessingFilter;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.handler.CredentialAuthenticationFailureHandler;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.handler.CredentialAuthenticationSuccessHandler;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.provider.CredentialSignInProvider;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.provider.CredentialSignUpProvider;
import com.seatliberator.seatliberator.identity.server.security.shared.response.ResponseWriter;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenResponseProcessor;
import com.seatliberator.seatliberator.starter.security.customizer.HttpSecurityCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
public class CredentialAuthenticationConfiguration {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    HttpSecurityCustomizer credentialHttpSecurityCustomizer(
            JsonCredentialSignInProcessingFilter jsonCredentialSignInProcessingFilter,
            JsonCredentialSignUpProcessingFilter jsonCredentialSignUpProcessingFilter
    ) {
        return httpSecurity -> httpSecurity
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/.well-known/jwks.json", "/api/v1/auth/**")
                        .permitAll()
                )
                .addFilterBefore(jsonCredentialSignInProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jsonCredentialSignUpProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    JsonCredentialSignUpProcessingFilter jsonCredentialSignUpProcessingFilter(
            @Qualifier("credential") AuthenticationManager credentialAuthenticationManager,
            CredentialAuthenticationSuccessHandler successHandler,
            CredentialAuthenticationFailureHandler failureHandler,
            ObjectMapper objectMapper
    ) {
        var matcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/v1/auth/sign-up");
        var filter = new JsonCredentialSignUpProcessingFilter(matcher, objectMapper);

        filter.setAuthenticationManager(credentialAuthenticationManager);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }

    @Bean
    JsonCredentialSignInProcessingFilter jsonCredentialSignInProcessingFilter(
            @Qualifier("credential") AuthenticationManager credentialAuthenticationManager,
            CredentialAuthenticationSuccessHandler successHandler,
            CredentialAuthenticationFailureHandler failureHandler,
            ObjectMapper objectMapper
    ) {
        var matcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/v1/auth/sign-in");
        var filter = new JsonCredentialSignInProcessingFilter(matcher, objectMapper);

        filter.setAuthenticationManager(credentialAuthenticationManager);
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }

    @Bean
    CredentialSignInProvider credentialSignInProvider(AuthenticationCredentialUseCase useCase) {
        return new CredentialSignInProvider(useCase);
    }

    @Bean
    CredentialSignUpProvider credentialSignUpProvider(RegisterCredentialAccountUseCase useCase) {
        return new CredentialSignUpProvider(useCase);
    }

    @Bean
    CredentialAuthenticationSuccessHandler credentialAuthenticationSuccessHandler(TokenResponseProcessor tokenIssueProcessor) {
        return new CredentialAuthenticationSuccessHandler(tokenIssueProcessor);
    }

    @Bean
    CredentialAuthenticationFailureHandler credentialAuthenticationFailureHandler(ResponseWriter responseWriter) {
        return new CredentialAuthenticationFailureHandler(responseWriter);
    }

    @Bean
    @Qualifier("credential")
    AuthenticationManager credentialAuthenticationManager(
            CredentialSignUpProvider credentialSignUpProvider,
            CredentialSignInProvider credentialSignInProvider
    ) {
        return new ProviderManager(List.of(
                credentialSignInProvider,
                credentialSignUpProvider
        ));
    }
}
