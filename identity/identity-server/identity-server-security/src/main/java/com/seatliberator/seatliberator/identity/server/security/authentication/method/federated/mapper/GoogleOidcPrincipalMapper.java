package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.CustomOidcPrincipal;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.FederatedPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class GoogleOidcPrincipalMapper implements FederatedPrincipalMapper {
    private static final String FALLBACK_NICKNAME = "user";

    @Override
    public String key() {
        return "google";
    }

    @Override
    public FederatedPrincipal resolve(OAuth2User oAuth2User) {
        if (!(oAuth2User instanceof OidcUser oidcUser))
            throw new IllegalArgumentException("Federated principal mapper requires OidcUser. registrationId=" + key());

        var claims = oidcUser.getClaims();

        var authorities = oidcUser.getAuthorities();
        var idToken = oidcUser.getIdToken();
        var userInfo = oidcUser.getUserInfo();

        var registrationId = key();
        var providerUserId = oidcUser.getSubject();
        var providerUserNickname = getNickname(claims);

        return new CustomOidcPrincipal(authorities, idToken, userInfo, registrationId, providerUserId, providerUserNickname);
    }

    private String getNickname(Map<String, Object> attributes) {
        var nickname = (String) attributes.get("name");
        var email = (String) attributes.get("email");

        if (StringUtils.hasText(nickname)) return nickname;
        if (!StringUtils.hasText(email)) return FALLBACK_NICKNAME;

        var emailParts = email.split(Pattern.quote("@"));
        if (emailParts.length != 2 || emailParts[0].isBlank()) return FALLBACK_NICKNAME;

        return emailParts[0];
    }
}
