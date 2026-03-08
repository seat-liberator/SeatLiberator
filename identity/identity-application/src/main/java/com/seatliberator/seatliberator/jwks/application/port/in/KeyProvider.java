package com.seatliberator.seatliberator.jwks.application.port.in;

public interface KeyProvider {
    KeyProviderResult getVerifiableKeys();
}
