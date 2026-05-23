package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class CredentialAuthenticate extends AbstractAuthenticationToken {
    private final AuthenticatedResult result;

    public CredentialAuthenticate(AuthenticatedResult result) {
        super(List.of());
        super.setAuthenticated(true);

        this.result = Preconditions.requireNonNull(result, "result");
    }

    public static CredentialAuthenticate of(AuthenticatedResult result) {
        return new CredentialAuthenticate(result);
    }

    @Override
    public Object getPrincipal() {
        return result;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
