package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

@Setter
public class CustomOidcPrincipal extends DefaultOidcUser implements FederatedPrincipal {
    private String registrationId;
    private String providerUserId;
    private String email;
    private String nickname;

    public CustomOidcPrincipal(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
        super(authorities, idToken, userInfo);
    }

    public CustomOidcPrincipal(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo, String nameAttributeKey) {
        super(authorities, idToken, userInfo, nameAttributeKey);
    }

    @Override
    public String registrationId() {
        return registrationId;
    }

    @Override
    public String providerUserId() {
        return providerUserId;
    }

    @Override
    public @Nullable String email() {
        return email;
    }

    @Override
    public @Nullable String nickname() {
        return nickname;
    }
}
