package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.IssuedTokenEntry;

public interface TokenIssueProcessor {
    IssuedTokenEntry process(String subject);
}
