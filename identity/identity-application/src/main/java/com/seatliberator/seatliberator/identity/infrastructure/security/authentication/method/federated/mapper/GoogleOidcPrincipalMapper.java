package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOidcPrincipal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

public class GoogleOidcPrincipalMapper implements FederatedPrincipalMapper {
    @Override
    public String key() {
        return "google";
    }

    @Override
    public FederatedPrincipal resolve(OidcUser oidcUser) {
        var claims = oidcUser.getClaims();

        var providerUserId = oidcUser.getSubject();
        var email = (String) claims.get("email");
        var nickname = String.valueOf(claims.getOrDefault("name", ""));

        CustomOidcPrincipal customOidcPrincipal = (CustomOidcPrincipal) oidcUser;

        customOidcPrincipal.setRegistrationId(key());
        customOidcPrincipal.setProviderUserId(providerUserId);
        customOidcPrincipal.setEmail(email);

        if (StringUtils.hasText(nickname)) {
            customOidcPrincipal.setNickname(nickname);
        }

        return customOidcPrincipal;
    }
}
