package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import org.jspecify.annotations.Nullable;

public interface FederatedPrincipalMapperRegistry {
    void registerProfileMapper(FederatedPrincipalMapper federatedPrincipalMapper);

    @Nullable FederatedPrincipalMapper resolve(String registrationId);
}
