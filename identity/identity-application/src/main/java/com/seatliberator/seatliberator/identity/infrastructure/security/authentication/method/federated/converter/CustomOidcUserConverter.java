package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.converter;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOidcPrincipal;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserSource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class CustomOidcUserConverter implements Converter<OidcUserSource, OidcUser> {
    @Override
    public OidcUser convert(OidcUserSource source) {
        OidcUserRequest userRequest = source.getUserRequest();
        OidcUserInfo userInfo = source.getUserInfo();
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo, userNameAttributeName));
        } else {
            authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
        }
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String scope : token.getScopes()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }
        if (StringUtils.hasText(userNameAttributeName)) {
            return new CustomOidcPrincipal(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        }
        return new CustomOidcPrincipal(authorities, userRequest.getIdToken(), userInfo);
    }
}
