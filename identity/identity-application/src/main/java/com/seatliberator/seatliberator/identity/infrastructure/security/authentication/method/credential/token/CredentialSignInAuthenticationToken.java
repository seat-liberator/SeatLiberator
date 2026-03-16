package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

@Getter
public class CredentialSignInAuthenticationToken extends AbstractAuthenticationToken {
    private String email;
    private String password;
    private String subject;

    public CredentialSignInAuthenticationToken(
            String email,
            String password
    ) {
        super(List.of());
        setAuthenticated(false);

        this.email = email;
        this.password = password;
    }

    public CredentialSignInAuthenticationToken(
            String subject
    ) {
        super(List.of());
        setAuthenticated(true);

        this.subject = subject;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.password = null;
    }

    @Override
    public @Nullable Object getCredentials() {
        return password;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return email;
    }
}
