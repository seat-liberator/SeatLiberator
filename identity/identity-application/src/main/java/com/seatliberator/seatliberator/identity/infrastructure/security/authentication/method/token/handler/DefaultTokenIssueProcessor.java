package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler;

import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.IssuedTokenEntry;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultTokenIssueProcessor implements TokenIssueProcessor {
    private final TokenProvider jwtProvider;
    private final TokenProvider opaquTokenProvider;

    @Override
    public IssuedTokenEntry process(String subject) {
        Map<String, Object> attribute = new HashMap<>();
        attribute.put("subject", subject);
        var accessToken = jwtProvider.issue(attribute);
        var refreshToken = opaquTokenProvider.issue(attribute);

        return new IssuedTokenEntry(
                accessToken,
                refreshToken
        );
    }
}
