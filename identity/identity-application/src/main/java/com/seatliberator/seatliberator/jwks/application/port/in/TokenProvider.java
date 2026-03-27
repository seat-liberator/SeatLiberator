package com.seatliberator.seatliberator.jwks.application.port.in;

import java.util.Map;

public interface TokenProvider {
    String issue(Map<String, Object> attributes);
}
