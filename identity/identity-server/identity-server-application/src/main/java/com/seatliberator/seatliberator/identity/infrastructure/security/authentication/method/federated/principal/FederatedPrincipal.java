package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal;

import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface FederatedPrincipal extends OAuth2User {
    String registrationId();

    String providerUserId();

    @Nullable String email();

    @Nullable String nickname();
}
