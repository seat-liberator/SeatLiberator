package com.seatliberator.seatliberator.identity.server.persistence.credential.jpa.repository;

import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CredentialAccountRepository extends JpaRepository<CredentialAccount, UUID>, JpaSpecificationExecutor<CredentialAccount> {
}