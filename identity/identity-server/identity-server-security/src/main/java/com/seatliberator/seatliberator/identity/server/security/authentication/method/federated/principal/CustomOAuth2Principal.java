package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Setter
public class CustomOAuth2Principal implements OAuth2User, FederatedPrincipal {
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String registrationId;
    private final String providerUserId;
    private final String providerUserNickname;

    public CustomOAuth2Principal(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String registrationId,
            String providerUserId,
            String providerUserNickname
    ) {
        this.authorities = Preconditions.requireNonNull(authorities, "authorities");
        this.attributes = Preconditions.requireNonNull(attributes, "attributes");

        this.registrationId = Preconditions.requireNonBlank(registrationId, "registrationId");
        this.providerUserId = Preconditions.requireNonBlank(providerUserId, "providerUserId");
        this.providerUserNickname = Preconditions.requireNonBlank(providerUserNickname, "providerUserNickname");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return providerUserNickname;
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
