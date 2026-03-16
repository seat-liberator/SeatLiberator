package com.seatliberator.seatliberator.identity.client.properties;


import java.net.URI;

public record ServerProperties(
        URI baseUrl,
        URI uri
) {
    public static ServerProperties getDefault() {
        return new ServerProperties(null, null);
    }

    public boolean isBaseUrlConfigured() {
        return baseUrl != null;
    }

    public boolean isUriConfigured() {
        return uri != null;
    }

    public boolean isAllConfigured() {
        return isBaseUrlConfigured() && isUriConfigured();
    }

    public URI resolve() {
        if (!isAllConfigured()) {
            throw new IllegalStateException("baseUrl and uri must both be configured");
        }
        return baseUrl.resolve(uri);
    }
}