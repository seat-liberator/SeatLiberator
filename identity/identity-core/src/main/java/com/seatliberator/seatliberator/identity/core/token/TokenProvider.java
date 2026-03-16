package com.seatliberator.seatliberator.identity.core.token;

import java.util.Map;

public interface TokenProvider {
    String issue(Map<String, Object> attributes);
}
