package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOidcPrincipal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

@Slf4j
public class GithubOAuth2PrincipalMapper implements FederatedPrincipalMapper {
    @Override
    public String key() {
        return "github";
    }

    @Override
    public FederatedPrincipal resolve(OidcUser oidcUser) {
        log.debug("Attempting federated principal mapping. registrationId={}", key());

        var attributes = oidcUser.getAttributes();

        var providerUserId = String.valueOf(attributes.get("id"));
        var email = (String) attributes.get("email");
        var nickname = (String) attributes.get("name");

        CustomOidcPrincipal customOidcPrincipal = (CustomOidcPrincipal) oidcUser;

        log.debug(
                "GitHub principal attributes resolved. registrationId={}, email={}, nicknamePresent={}",
                key(),
                email,
                StringUtils.hasText(nickname)
        );

        customOidcPrincipal.setRegistrationId(key());
        customOidcPrincipal.setProviderUserId(providerUserId);
        customOidcPrincipal.setEmail(email);

        if (StringUtils.hasText(nickname)) {
            customOidcPrincipal.setNickname(nickname);
        }

        log.debug(
                "GitHub federated principal mapping succeeded. registrationId={}, email={}",
                key(),
                email
        );

        return customOidcPrincipal;
    }
}
