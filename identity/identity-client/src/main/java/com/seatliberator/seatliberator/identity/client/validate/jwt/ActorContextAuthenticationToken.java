package com.seatliberator.seatliberator.identity.client.validate.jwt;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class ActorContextAuthenticationToken extends AbstractAuthenticationToken {
    private final Actor principal;
    private final Jwt jwt;

    public ActorContextAuthenticationToken(
            Actor principal,
            Jwt jwt,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        this.jwt = jwt;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }

    @Override
    public @Nullable Object getCredentials() {
        return jwt;
    }

    @Override
    public @NullMarked String getName() {
        return principal.subject();
    }
}
