package com.seatliberator.seatliberator.identity.server.application.token.internal;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.token.config.TokenProperties;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccessTokenIssuer {
    private final UserReader userReader;
    private final UserGrantedRoleReader grantedRoleReader;
    private final NamespaceRoleSerializer roleSerializer;
    private final JwtEncoder jwtEncoder;
    private final TokenProperties tokenProperties;

    public Jwt issue(UUID userId, Instant issuedAt) {
        var user = userReader.findById(userId)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND));

        var grantedRoles = grantedRoleReader.findByUserId(userId);

        var subject = user.getId().toString();
        var nickname = user.getNickname();
        var scopes = grantedRoles.stream()
                .map(grant -> roleSerializer.serialize(grant.getNamespaceRole()))
                .collect(Collectors.toUnmodifiableSet());

        var properties = tokenProperties.accessToken();
        var expiresAt = issuedAt.plus(properties.ttl());
        var claimsSet = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(subject)
                .audience(properties.audience())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("nickname", nickname)
                .claim("scopes", scopes)
                .build();

        var parameters = JwtEncoderParameters.from(claimsSet);

        return jwtEncoder.encode(parameters);
    }
}
