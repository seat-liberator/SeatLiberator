package com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.identity.application.port.out.AuthContextRecord;
import com.seatliberator.seatliberator.identity.application.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.domain.FederatedAccount;
import com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa.repository.FederatedAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFederatedAccountStore implements FederatedAccountStore {
    private final FederatedAccountRepository federatedAccountRepository;

    @Override
    public Optional<FederatedAccount> findByRegistrationIdAndProviderUserId(String registrationId, String providerUserId) {
        return federatedAccountRepository.findByRegistrationIdAndProviderUserId(registrationId, providerUserId);
    }

    @Override
    public Optional<AuthContextRecord.Federated> findFederatedAuthByRegistrationIdAndProviderUserId(String registrationId, String providerUserId) {
        return federatedAccountRepository.findAuthByRegistrationIdAndProviderUserId(
                        registrationId,
                        providerUserId
                )
                .map(federatedAccount -> new AuthContextRecord.Federated(
                        federatedAccount.getUser().getId(),
                        federatedAccount.getRegistrationId(),
                        federatedAccount.getProviderUserId()
                ));
    }

    @Override
    public boolean existsByRegistrationIdAndProviderUserId(String registrationId, String providerUserId) {
        return federatedAccountRepository.existsByRegistrationIdAndProviderUserId(
                registrationId,
                providerUserId
        );
    }

    @Override
    public FederatedAccount save(FederatedAccount federatedAccount) {
        return federatedAccountRepository.save(federatedAccount);
    }
}
