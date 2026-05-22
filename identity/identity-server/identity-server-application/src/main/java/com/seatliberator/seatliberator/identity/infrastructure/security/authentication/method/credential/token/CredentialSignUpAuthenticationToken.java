package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class CredentialSignUpAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String nickname;
    private final String email;
    private Object credentials;

    public CredentialSignUpAuthenticationToken(
            Object principal,
            Object credentials,
            String nickname,
            String email,
            Collection<? extends GrantedAuthority> authorities,
            boolean authenticated
    ) {
        super(authorities);
        super.setAuthenticated(authenticated);

        this.principal = principal;
        this.credentials = credentials;
        this.nickname = nickname;
        this.email = email;
    }

    public static CredentialSignUpAuthenticationToken unauthenticated(
            String nickname,
            String email,
            String password
    ) {
        return new CredentialSignUpAuthenticationToken(
                email,
                password,
                nickname,
                email,
                List.of(),
                false
        );
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }

    @Override
    public @Nullable Object getCredentials() {
        return credentials;
    }
}
