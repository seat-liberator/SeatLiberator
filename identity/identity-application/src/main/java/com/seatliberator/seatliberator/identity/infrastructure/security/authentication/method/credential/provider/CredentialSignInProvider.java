package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.CredentialSignInAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CredentialSignInProvider implements AuthenticationProvider {
    private final AccountAuthenticator accountAuthenticator;

    @Override
    public @Nullable Authentication authenticate(
            @NonNull Authentication authentication
    ) throws AuthenticationException {
        var signInToken = (CredentialSignInAuthenticationToken) authentication;

        var email = signInToken.getEmail();
        var password = signInToken.getPassword();

        var authenticationCommand = new AuthenticationCommand.Credential(
                email,
                password
        );
        var authEntry = accountAuthenticator.authenticate(authenticationCommand);

        var trustedUserPrincipal = new TrustedUserPrincipal(
                authEntry.userId().toString(),
                authEntry.nickname()
        );

        return new TrustedAuthenticationToken(
                trustedUserPrincipal,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return false;
    }
}
