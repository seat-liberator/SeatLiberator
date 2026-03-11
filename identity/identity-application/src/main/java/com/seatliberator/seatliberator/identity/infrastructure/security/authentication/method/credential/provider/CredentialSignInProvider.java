package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.application.exception.ApplicationException;
import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.CredentialSignInAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
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
        log.debug("Attempting credential sign-in authentication provider processing.");

        var signInToken = (CredentialSignInAuthenticationToken) authentication;

        var email = signInToken.getEmail();
        var password = signInToken.getPassword();

        log.debug("Credential sign-in token received. email={}", email);

        var authenticationCommand = new AuthenticationCommand.Credential(email, password);
        log.debug("Credential authentication command created. email={}", email);

        AuthEntry authEntry;
        try {
            authEntry = accountAuthenticator.authenticate(authenticationCommand);
            log.debug(
                    "Credential authentication application service succeeded. email={}, userId={}",
                    email,
                    authEntry.userId()
            );
        } catch (ApplicationException e) {
            log.debug(
                    "Credential sign-in failed in application service. email={}, errorCode={}",
                    email,
                    e.getErrorCode()
            );

            throw switch (e.getErrorCode()) {
                case AUTHENTICATION_FAILED -> new AuthenticationServiceException(e.getMessage());
                default -> new AuthenticationServiceException("Credential sign-up failed", e);
            };
        }

        var trustedUserPrincipal = new TrustedUserPrincipal(
                authEntry.userId().toString(),
                authEntry.nickname()
        );
        log.debug(
                "Trusted user principal created for credential sign-in. email={}, userId={}",
                email,
                authEntry.userId()
        );

        var trustedAuthentication = new TrustedAuthenticationToken(
                trustedUserPrincipal,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        log.debug(
                "Credential sign-in trusted authentication token created. email={}, userId={}",
                email,
                authEntry.userId()
        );

        return trustedAuthentication;
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CredentialSignInAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
