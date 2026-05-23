package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationCredentialCommand;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

@Getter
public class CredentialSignInAuthentication extends AbstractAuthenticationToken {
    private final String email;
    private String password;

    public CredentialSignInAuthentication(String email, String password) {
        super(List.of());
        setAuthenticated(false);

        this.email = Preconditions.requireNonBlank(email, "email");
        this.password = Preconditions.requireNonBlank(password, "password");
    }

    public static CredentialSignInAuthentication of(String email, String password) {
        return new CredentialSignInAuthentication(email, password);
    }

    public AuthenticationCredentialCommand toCommand() {
        return AuthenticationCredentialCommand.of(email, password);
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
