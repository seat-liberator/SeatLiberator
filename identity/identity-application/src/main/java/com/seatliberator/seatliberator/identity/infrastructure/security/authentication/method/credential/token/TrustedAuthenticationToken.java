package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class TrustedAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;

    public TrustedAuthenticationToken(
            TrustedUserPrincipal trustedUserPrincipal,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        super.setAuthenticated(true);

        this.principal = trustedUserPrincipal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
