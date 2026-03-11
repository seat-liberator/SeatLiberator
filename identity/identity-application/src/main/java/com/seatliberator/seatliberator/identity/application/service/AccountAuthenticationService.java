package com.seatliberator.seatliberator.identity.application.service;

import com.seatliberator.seatliberator.identity.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.identity.application.exception.ApplicationException;
import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.application.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.FederatedAccountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountAuthenticationService implements AccountAuthenticator {
    private final CredentialAccountStore credentialAccountStore;
    private final FederatedAccountStore federatedAccountStore;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthEntry authenticate(AuthenticationCommand.Credential command) {
        return credentialAccountStore.findCredentialAuthByEmail(command.email())
                .filter(auth -> passwordEncoder.matches(command.password(), auth.passwordHash()))
                .map(auth -> new AuthEntry(
                        auth.userId(),
                        auth.nickname()
                ))
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED));
    }

    @Override
    public AuthEntry authenticate(AuthenticationCommand.Federated command) {
        return federatedAccountStore.findFederatedAuthByRegistrationIdAndProviderUserId(
                        command.registrationId(),
                        command.providerUserId()
                )
                .map(auth -> new AuthEntry(
                        auth.userId(),
                        auth.nickname()
                ))
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED));
    }
}
