package com.seatliberator.seatliberator.identity.server.application.federated.port.out;

import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountUserRegistrationLookupCriteria;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;

import java.util.Optional;
import java.util.UUID;

public interface FederatedAccountReader {
    boolean existsById(UUID id);

    boolean existsByCriteria(FederatedAccountLookupCriteria criteria);

    Optional<FederatedAccount> findById(UUID id);

    Optional<FederatedAccount> findByCriteria(FederatedAccountLookupCriteria criteria);

    Optional<FederatedAccount> findByCriteria(FederatedAccountUserRegistrationLookupCriteria criteria);
}
