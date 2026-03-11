package com.seatliberator.seatliberator.identity.application.service;

import com.seatliberator.seatliberator.identity.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.identity.application.exception.ApplicationException;
import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.application.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.FederatedAccountStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountAuthenticationService implements AccountAuthenticator {
    private final CredentialAccountStore credentialAccountStore;
    private final FederatedAccountStore federatedAccountStore;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthEntry authenticate(AuthenticationCommand.Credential command) {
        log.debug("Attempting credential authentication. email={}", command.email());

        var auth = credentialAccountStore.findCredentialAuthByEmail(command.email())
                .orElseThrow(() -> {
                    log.debug("Credential authentication failed because account was not found. email={}", command.email());
                    return new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED);
                });

        log.debug(
                "Credential account found. email={}, userId={}",
                command.email(),
                auth.userId()
        );

        if (!passwordEncoder.matches(command.password(), auth.passwordHash())) {
            log.debug(
                    "Credential authentication failed due to password mismatch. email={}, userId={}",
                    command.email(),
                    auth.userId()
            );
            throw new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED);
        }

        log.debug(
                "Credential authentication succeeded. email={}, userId={}",
                command.email(),
                auth.userId()
        );

        return new AuthEntry(
                auth.userId(),
                auth.nickname()
        );
    }

    @Override
    public AuthEntry authenticate(AuthenticationCommand.Federated command) {
        log.debug(
                "Attempting federated authentication. registrationId={}",
                command.registrationId()
        );

        var auth = federatedAccountStore.findFederatedAuthByRegistrationIdAndProviderUserId(
                        command.registrationId(),
                        command.providerUserId()
                )
                .orElseThrow(() -> {
                    log.debug(
                            "Federated authentication failed because account was not found. registrationId={}, providerUserId={}",
                            command.registrationId(),
                            command.providerUserId()
                    );
                    return new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED);
                });

        log.debug(
                "Federated account found. registrationId={}, userId={}",
                command.registrationId(),
                auth.userId()
        );

        log.debug(
                "Federated authentication succeeded. registrationId={}, userId={}",
                command.registrationId(),
                auth.userId()
        );

        return new AuthEntry(
                auth.userId(),
                auth.nickname()
        );
    }
}
