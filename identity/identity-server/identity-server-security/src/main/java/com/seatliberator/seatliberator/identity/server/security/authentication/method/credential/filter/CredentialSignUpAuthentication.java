package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter;

import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.RegisterCredentialAccountCommand;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

@Getter
public class CredentialSignUpAuthentication extends AbstractAuthenticationToken {
    private final String nickname;
    private final String email;
    private String password;

    public CredentialSignUpAuthentication(String nickname, String email, String password) {
        super(List.of());
        setAuthenticated(false);

        this.nickname = Preconditions.requireNonBlank(nickname, "nickname");
        this.email = Preconditions.requireNonBlank(email, "email");
        this.password = Preconditions.requireNonBlank(password, "password");
    }

    public static CredentialSignUpAuthentication of(String nickname, String email, String password) {
        return new CredentialSignUpAuthentication(nickname, email, password);
    }

    public RegisterCredentialAccountCommand toCommand() {
        return RegisterCredentialAccountCommand.of(nickname, email, password);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.password = null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return email;
    }

    @Override
    public @Nullable Object getCredentials() {
        return password;
    }
}
