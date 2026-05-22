package com.seatliberator.seatliberator.identity.application.service;

import com.seatliberator.seatliberator.identity.application.port.in.AccountExistenceChecker;
import com.seatliberator.seatliberator.identity.application.port.in.command.ExistenceCheckingCommand;
import com.seatliberator.seatliberator.identity.application.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.FederatedAccountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountExistenceCheckerService implements AccountExistenceChecker {
    private final CredentialAccountStore credentialAccountStore;
    private final FederatedAccountStore federatedAccountStore;

    @Override
    public boolean isCredentialAccountExists(ExistenceCheckingCommand.Credential command) {
        return credentialAccountStore.existsByEmail(command.email());
    }

    @Override
    public boolean isFederatedAccountExists(ExistenceCheckingCommand.Federated command) {
        return federatedAccountStore.existsByRegistrationIdAndProviderUserId(
                command.registrationId(),
                command.providerUserId()
        );
    }
}
