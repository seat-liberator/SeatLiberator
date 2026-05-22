package com.seatliberator.seatliberator.identity.server.application.jwks.port.in;

import java.util.Map;

public interface TokenProvider {
    String issue(Map<String, Object> attributes);
}
