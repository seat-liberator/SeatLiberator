package com.seatliberator.seatliberator.identity.application.port.out;

import com.seatliberator.seatliberator.identity.domain.FederatedAccount;

import java.util.Optional;

public interface FederatedAccountStore {
    Optional<FederatedAccount> findByRegistrationIdAndProviderUserId(
            String registrationId,
            String providerUserId
    );

    Optional<AuthRecord.Federated> findFederatedAuthByRegistrationIdAndProviderUserId(
            String registrationId,
            String providerUserId
    );

    boolean existsByRegistrationIdAndProviderUserId(
            String registrationId,
            String providerUserId
    );

    FederatedAccount save(FederatedAccount federatedAccount);
}
