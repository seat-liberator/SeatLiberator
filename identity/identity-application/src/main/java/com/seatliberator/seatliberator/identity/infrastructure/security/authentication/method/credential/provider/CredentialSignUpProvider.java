package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.application.exception.ApplicationException;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.CredentialSignUpAuthenticationToken;
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
public class CredentialSignUpProvider implements AuthenticationProvider {
    private final UserRegistrar userRegistrar;

    @Override
    public @Nullable Authentication authenticate(
            @NonNull Authentication authentication
    ) throws AuthenticationException {
        log.debug("Attempting credential sign-up authentication provider processing.");

        var signUpToken = (CredentialSignUpAuthenticationToken) authentication;

        var nickname = signUpToken.getNickname();
        var email = signUpToken.getEmail();
        var password = (String) signUpToken.getCredentials();

        log.debug("Credential sign-up token received. email={}, nickname={}", email, nickname);

        var registrationCommand = new RegistrationCommand.Credential(
                nickname,
                email,
                password
        );
        log.debug("Credential registration command created. email={}, nickname={}", email, nickname);

        AuthEntry authEntry;

        try {
            authEntry = userRegistrar.register(registrationCommand);
            log.debug(
                    "Credential registration application service succeeded. email={}, userId={}",
                    email,
                    authEntry.userId()
            );
        } catch (ApplicationException e) {
            log.debug(
                    "Credential sign-up failed in application service. email={}, errorCode={}",
                    email,
                    e.getErrorCode()
            );

            throw switch (e.getErrorCode()) {
                case AUTHENTICATION_FAILED, EMAIL_DUPLICATED -> new AuthenticationServiceException(e.getMessage());
                default -> new AuthenticationServiceException("Credential sign-up failed", e);
            };
        }

        var trustedUserPrincipal = new TrustedUserPrincipal(
                authEntry.userId().toString(),
                authEntry.nickname()
        );
        log.debug(
                "Trusted user principal created for credential sign-up. email={}, userId={}",
                email,
                authEntry.userId()
        );

        var trustedAuthentication = new TrustedAuthenticationToken(
                trustedUserPrincipal,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        log.debug(
                "Credential sign-up trusted authentication token created. email={}, userId={}",
                email,
                authEntry.userId()
        );

        return trustedAuthentication;
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CredentialSignUpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
