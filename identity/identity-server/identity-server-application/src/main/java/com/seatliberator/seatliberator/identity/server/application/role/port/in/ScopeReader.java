package com.seatliberator.seatliberator.identity.server.application.role.port.in;

import java.util.Set;

public interface ScopeReader {
    Set<String> readScopes(String userId);
}
