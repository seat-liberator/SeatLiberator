package com.seatliberator.seatliberator.role.application.port.in;

import java.util.Set;

public interface ScopeReader {
    Set<String> readScopes(String userId);
}
