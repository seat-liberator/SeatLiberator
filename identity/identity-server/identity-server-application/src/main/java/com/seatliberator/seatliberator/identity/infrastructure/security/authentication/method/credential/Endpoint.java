package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential;

public record Endpoint(
        String method,
        String uri
) {
}
