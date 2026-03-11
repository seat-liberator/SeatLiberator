package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface FederatedPrincipalMapper {
    String key();

    FederatedPrincipal resolve(OidcUser oidcUser);
}
