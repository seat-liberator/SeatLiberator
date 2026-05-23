package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.RegisterCredentialAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter.CredentialSignUpAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class CredentialSignUpProvider implements AuthenticationProvider {
    private final RegisterCredentialAccountUseCase useCase;

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        final AuthenticatedResult result;
        try {
            var authenticate = (CredentialSignUpAuthentication) authentication;
            result = useCase.register(authenticate.toCommand());
        } catch (IdentityApplicationException e) {
            throw switch (e.getErrorCode()) {
                case AUTHENTICATION_FAILED, EMAIL_DUPLICATED -> new AuthenticationServiceException(e.getMessage());
                default -> new AuthenticationServiceException("Credential sign up failed", e);
            };
        }

        return CredentialAuthenticate.of(result);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CredentialSignUpAuthentication.class.isAssignableFrom(authentication);
    }
}
