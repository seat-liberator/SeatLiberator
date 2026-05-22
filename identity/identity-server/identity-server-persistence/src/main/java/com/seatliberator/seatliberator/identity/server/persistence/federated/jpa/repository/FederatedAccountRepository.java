package com.seatliberator.seatliberator.identity.server.persistence.federated.jpa.repository;

import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FederatedAccountRepository extends JpaRepository<FederatedAccount, UUID>, JpaSpecificationExecutor<FederatedAccount> {
}
