package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOidcPrincipal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

@Slf4j
public class GoogleOidcPrincipalMapper implements FederatedPrincipalMapper {
    @Override
    public String key() {
        return "google";
    }

    @Override
    public FederatedPrincipal resolve(OAuth2User oAuth2User) {
        log.debug("Attempting federated principal mapping. registrationId={}", key());

        if (!(oAuth2User instanceof OidcUser oidcUser)) {
            log.debug(
                    "Google federated principal mapping failed because oauth2 user was not OIDC user. registrationId={}, principalType={}",
                    key(),
                    oAuth2User == null ? "null" : oAuth2User.getClass().getName()
            );
            throw new IllegalArgumentException("Google federated principal mapping requires OidcUser");
        }

        var claims = oidcUser.getClaims();

        var providerUserId = oidcUser.getSubject();
        var email = (String) claims.get("email");
        var nickname = String.valueOf(claims.getOrDefault("name", ""));

        log.debug(
                "Google principal claims resolved. registrationId={}, email={}, nicknamePresent={}",
                key(),
                email,
                StringUtils.hasText(nickname)
        );

        var principal = new CustomOidcPrincipal(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );

        principal.setRegistrationId(key());
        principal.setProviderUserId(providerUserId);
        principal.setEmail(email);

        if (StringUtils.hasText(nickname)) {
            principal.setNickname(nickname);
        }

        log.debug(
                "Google federated principal mapping succeeded. registrationId={}, email={}, nicknamePresent={}",
                key(),
                email,
                StringUtils.hasText(nickname)
        );

        return principal;
    }
}
