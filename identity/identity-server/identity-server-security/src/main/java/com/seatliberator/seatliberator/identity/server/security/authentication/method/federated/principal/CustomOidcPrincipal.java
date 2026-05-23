package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

@Setter
public class CustomOidcPrincipal extends DefaultOidcUser implements FederatedPrincipal {
    private final String registrationId;
    private final String providerUserId;
    private final String providerUserNickname;

    public CustomOidcPrincipal(
            Collection<? extends GrantedAuthority> authorities,
            OidcIdToken idToken,
            OidcUserInfo userInfo,
            String registrationId,
            String providerUserId,
            String providerUserNickname
    ) {
        super(authorities, idToken, userInfo);
        this.registrationId = Preconditions.requireNonBlank(registrationId, "registrationId");
        this.providerUserId = Preconditions.requireNonBlank(providerUserId, "providerUserId");
        this.providerUserNickname = Preconditions.requireNonBlank(providerUserNickname, "providerUserNickname");
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
    public String providerUserNickname() {
        return providerUserNickname;
    }
}
