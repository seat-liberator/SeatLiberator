package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationCredentialUseCase;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter.CredentialSignInAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class CredentialSignInProvider implements AuthenticationProvider {
    private final AuthenticationCredentialUseCase useCase;

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        final AuthenticatedResult result;
        try {
            var authenticate = (CredentialSignInAuthentication) authentication;
            result = useCase.authenticate(authenticate.toCommand());
        } catch (IdentityApplicationException e) {
            var errorCode = e.getErrorCode();
            if (errorCode == IdentityApplicationErrorCode.AUTHENTICATION_FAILED) {
                throw new BadCredentialsException(e.getMessage());
            }
            throw new AuthenticationServiceException("Credential sign in failed.", e);
        }

        return CredentialAuthenticate.of(result);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CredentialSignInAuthentication.class.isAssignableFrom(authentication);
    }
}
