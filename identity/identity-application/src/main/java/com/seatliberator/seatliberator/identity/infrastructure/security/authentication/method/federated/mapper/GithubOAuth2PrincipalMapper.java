package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOidcPrincipal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class GithubOAuth2PrincipalMapper implements FederatedPrincipalMapper {
    @Override
    public String key() {
        return "github";
    }

    @Override
    public FederatedPrincipal resolve(OidcUser oidcUser) {
        var attributes = oidcUser.getAttributes();

        var providerUserId = String.valueOf(attributes.get("id"));
        var email = (String) attributes.get("email");
        var nickname = (String) attributes.get("name");

        CustomOidcPrincipal customOidcPrincipal = (CustomOidcPrincipal) oidcUser;

        customOidcPrincipal.setRegistrationId(key());
        customOidcPrincipal.setProviderUserId(providerUserId);
        customOidcPrincipal.setEmail(email);
        customOidcPrincipal.setNickname(nickname);

        return customOidcPrincipal;
    }
}
