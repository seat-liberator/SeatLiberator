package com.seatliberator.seatliberator.identity.server.security.shared.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.seatliberator.seatliberator.identity.server.application.jwks.port.out.KeyStore;
import com.seatliberator.seatliberator.identity.server.application.jwks.service.JwtProvider;
import com.seatliberator.seatliberator.identity.server.application.jwks.service.OpaqueTokenProvider;
import com.seatliberator.seatliberator.identity.server.security.shared.response.ResponseWriter;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenResponseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityConfigurationProperties.class)
public class SecurityConfiguration {

    @Bean
    @ConditionalOnBean(KeyStore.class)
    @ConditionalOnMissingBean(JwtDecoder.class)
    JwtDecoder jwtDecoder(KeyStore keyStore) {
        JWKSource<SecurityContext> source = (selector, context) -> {
            var keys = keyStore.getAllVerifiableKey().stream()
                    .map(key -> new RSAKey.Builder(key.getRsaPublicKey())
                            .keyID(key.getKid())
                            .keyUse(KeyUse.SIGNATURE)
                            .algorithm(JWSAlgorithm.RS256)
                            .build())
                    .map(JWK.class::cast)
                    .toList();

            return selector.select(new JWKSet(keys));
        };

        return NimbusJwtDecoder.withJwkSource(source).build();
    }

    @Bean
    ResponseWriter responseWriter(ObjectMapper objectMapper) {
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
