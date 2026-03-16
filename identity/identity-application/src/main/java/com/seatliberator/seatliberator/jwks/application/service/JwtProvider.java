package com.seatliberator.seatliberator.jwks.application.service;

import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtProvider implements TokenProvider {
    private final JwtEncoder jwtEncoder;
    private final Duration expiration;
    private final Clock clock;

    @Override
    public String issue(Map<String, Object> attributes) {
        var now = clock.instant();

        Object rawSubject = attributes.get("subject");
        if (!(rawSubject instanceof String subject) || !StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("subject must be non-empty string");
        }

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("seat-liberator-identity")
                .subject(subject)
                .audience(List.of("api"))
                .issuedAt(now)
                .expiresAt(now.plus(expiration))
                .build();

        var parameters = JwtEncoderParameters.from(claimsSet);
        return jwtEncoder.encode(parameters).getTokenValue();
    }
}