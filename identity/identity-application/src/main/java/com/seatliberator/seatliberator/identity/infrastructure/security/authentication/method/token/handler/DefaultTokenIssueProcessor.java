package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler;

import com.seatliberator.seatliberator.jwks.application.port.in.TokenProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.IssuedTokenEntry;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DefaultTokenIssueProcessor implements TokenIssueProcessor {
    private static final Set<String> DEFAULT_SCOPES = Set.of(
            "board.category.manage",
            "board.post.create"
    );

    private final TokenProvider jwtProvider;
    private final TokenProvider opaquTokenProvider;

    @Override
    public IssuedTokenEntry process(String subject) {
        Map<String, Object> attribute = new HashMap<>();
        attribute.put("subject", subject);
        attribute.put("scopes", new LinkedHashSet<>(DEFAULT_SCOPES));

        var accessToken = jwtProvider.issue(attribute);
        var refreshToken = opaquTokenProvider.issue(attribute);

        return new IssuedTokenEntry(
                accessToken,
                refreshToken
        );
    }
}
