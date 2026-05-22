package com.seatliberator.seatliberator.identity.server.application.federated.port.out;

import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;

public interface FederatedAccountStore {
    FederatedAccount save(FederatedAccount federatedAccount);

    void delete(FederatedAccount federatedAccount);
}
