package com.seatliberator.seatliberator.identity.server.application.account.factory;

import com.seatliberator.seatliberator.identity.server.application.account.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.ScopeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthEntryFactory {
    private final ScopeReader scopeReader;

    public AuthEntry create(UUID userId, String nickname) {
        var scope = scopeReader.readScopes(userId.toString());
        return new AuthEntry(userId, nickname, scope);
    }
}
