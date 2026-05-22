package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.IssuedTokenEntry;
import com.seatliberator.seatliberator.jwks.application.port.in.TokenProvider;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DefaultTokenIssueProcessor implements TokenIssueProcessor {
    private final TokenProvider jwtProvider;
    private final TokenProvider opaquTokenProvider;

    @Override
    public IssuedTokenEntry process(String subject, Set<String> scopes) {
        Map<String, Object> attribute = new HashMap<>();
        attribute.put("subject", subject);
        attribute.put("scopes", scopes);

        var accessToken = jwtProvider.issue(attribute);
        var refreshToken = opaquTokenProvider.issue(attribute);

        return new IssuedTokenEntry(
                accessToken,
                refreshToken
        );
    }
}
