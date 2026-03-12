package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface FederatedPrincipalMapper {
    String key();

    FederatedPrincipal resolve(OAuth2User oAuth2User);
}
