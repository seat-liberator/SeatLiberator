package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOAuth2Principal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

@Slf4j
public class GithubOAuth2PrincipalMapper implements FederatedPrincipalMapper {
    @Override
    public String key() {
        return "github";
    }

    @Override
    public FederatedPrincipal resolve(OAuth2User oAuth2User) {
        log.debug("Attempting federated principal mapping. registrationId={}", key());

        var attributes = oAuth2User.getAttributes();

        var providerUserId = String.valueOf(attributes.get("id"));
        var email = (String) attributes.get("email");
        var nickname = (String) attributes.get("name");

        log.debug(
                "GitHub principal attributes resolved. registrationId={}, email={}, nicknamePresent={}",
                key(),
                email,
                StringUtils.hasText(nickname)
        );

        var principal = new CustomOAuth2Principal(
                oAuth2User.getAuthorities(),
                attributes
        );
        principal.setRegistrationId(key());
        principal.setProviderUserId(providerUserId);
        principal.setEmail(email);

        if (StringUtils.hasText(nickname)) {
            principal.setNickname(nickname);
        }

        log.debug(
                "GitHub federated principal mapping succeeded. registrationId={}, email={}",
                key(),
                email
        );

        return principal;
    }
}
