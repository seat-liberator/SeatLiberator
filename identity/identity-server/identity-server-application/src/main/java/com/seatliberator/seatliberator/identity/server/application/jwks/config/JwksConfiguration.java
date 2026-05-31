package com.seatliberator.seatliberator.identity.server.application.jwks.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.seatliberator.seatliberator.identity.server.application.jwks.port.in.KeyProvider;
import com.seatliberator.seatliberator.identity.server.domain.jwks.RSASignatureKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.SecureRandom;
import java.time.Clock;
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
    RandomGenerator randomGenerator() {
        return new SecureRandom();
    }
}
