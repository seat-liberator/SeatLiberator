package com.seatliberator.seatliberator.jwks.application.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import com.seatliberator.seatliberator.jwks.application.port.in.KeyProvider;
import com.seatliberator.seatliberator.jwks.application.port.out.ByteEncoder;
import com.seatliberator.seatliberator.jwks.application.service.JwtProvider;
import com.seatliberator.seatliberator.jwks.application.service.OpaqueTokenProvider;
import com.seatliberator.seatliberator.jwks.domain.RSASignatureKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.random.RandomGenerator;

@Configuration
@EnableConfigurationProperties({JwksProperties.class, JwtProperties.class})
public class JwksConfiguration {
    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock systemClock() {
        return Clock.systemUTC();
    }

    @Bean
    JwtEncoder jwtEncoder(
            KeyProvider keyProvider
    ) {
        RSASignatureKey signatureKey = keyProvider.getSignatureKey();

        RSAKey rsaKey = new RSAKey.Builder(signatureKey.getRsaPublicKey())
                .privateKey(signatureKey.getRsaPrivateKey())
                .keyID(signatureKey.getKid())
                .build();

        JWKSource<SecurityContext> jwkSource = (selector, context) -> selector.select(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    TokenProvider jwtProvider(
            JwtEncoder jwtEncoder,
            JwtProperties properties,
            Clock clock
    ) {
        Duration expiration = properties.expiration();

        return new JwtProvider(
                jwtEncoder,
                expiration,
                clock
        );
    }

    @Bean
    RandomGenerator randomGenerator() {
        return new SecureRandom();
    }

    @Bean
    ByteEncoder byteEncoder() {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder::encodeToString;
    }

    @Bean
    TokenProvider opaqueTokenProvider(
            RandomGenerator randomGenerator,
            ByteEncoder byteEncoder
    ) {
        return new OpaqueTokenProvider(
                randomGenerator,
                byteEncoder
        );
    }
}
