package com.seatliberator.seatliberator.identity.server.security.shared.config;

import com.seatliberator.seatliberator.identity.server.application.jwks.service.JwtProvider;
import com.seatliberator.seatliberator.identity.server.application.jwks.service.OpaqueTokenProvider;
import com.seatliberator.seatliberator.identity.server.security.shared.response.ResponseWriter;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenResponseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityConfigurationProperties.class)
public class SecurityConfiguration {
    @Bean
    @Qualifier("custom")
    CorsConfigurationSource corsConfigurationSource(
            SecurityConfigurationProperties properties
    ) {
        var configuration = new CorsConfiguration();


        configuration.setAllowedOrigins(properties.allowedOrigins());
        configuration.setAllowedMethods(properties.allowedMethods());
        configuration.setAllowedHeaders(properties.allowedHeaders());
        configuration.setAllowCredentials(properties.allowCredentials());

        configuration.setExposedHeaders(properties.exposedHeaders());

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    ResponseWriter responseWriter(
            ObjectMapper objectMapper
    ) {
        return new ResponseWriter(objectMapper);
    }

    @Bean
    TokenResponseProcessor tokenResponseProcessor(
            JwtProvider jwtProvider,
            OpaqueTokenProvider opaqueTokenProvider,
            ResponseWriter responseWriter
    ) {
        return new TokenResponseProcessor(jwtProvider, opaqueTokenProvider, responseWriter);
    }
}
