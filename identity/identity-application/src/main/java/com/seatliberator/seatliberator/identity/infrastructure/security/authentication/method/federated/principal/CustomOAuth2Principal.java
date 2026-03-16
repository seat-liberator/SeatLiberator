package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Setter
public class CustomOAuth2Principal implements OAuth2User, FederatedPrincipal {
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    private String registrationId;
    private String providerUserId;
    private String email;
    private String nickname;

    public CustomOAuth2Principal(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes
    ) {
        this.authorities = authorities;
        this.attributes = attributes;
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
        return providerUserId;
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
