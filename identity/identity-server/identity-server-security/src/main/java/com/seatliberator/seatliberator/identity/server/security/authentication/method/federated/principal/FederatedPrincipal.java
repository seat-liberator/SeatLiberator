package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal;

public interface FederatedPrincipal {
    String registrationId();

    String providerUserId();

    String providerUserNickname();
}
