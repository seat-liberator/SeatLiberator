package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.IssuedTokenEntry;

import java.util.Set;

public interface TokenIssueProcessor {
    IssuedTokenEntry process(String subject, Set<String> scopes);
}
