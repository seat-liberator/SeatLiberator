package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties(prefix = "identity.application.auth.credential")
public record CredentialAuthenticationConfigurationProperties(
        Endpoint signIn,
        Endpoint signUp
) {
    public CredentialAuthenticationConfigurationProperties {
        signIn = Optional.ofNullable(signIn)
                .orElse(new Endpoint("POST", "/auth/sign-in"));
        signUp = Optional.ofNullable(signUp)
                .orElse(new Endpoint("POST", "/auth/sign-up"));
    }
}
