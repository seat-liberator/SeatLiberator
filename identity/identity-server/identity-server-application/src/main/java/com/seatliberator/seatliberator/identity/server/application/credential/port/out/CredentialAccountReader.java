package com.seatliberator.seatliberator.identity.server.application.credential.port.out;

import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountUserCriteria;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;

import java.util.Optional;
import java.util.UUID;

public interface CredentialAccountReader {
    boolean existsById(UUID id);

    boolean existsByCriteria(CredentialAccountEmailCriteria criteria);

    Optional<CredentialAccount> findById(UUID id);

    Optional<CredentialAccount> findByCriteria(CredentialAccountEmailCriteria criteria);

    Optional<CredentialAccount> findByCriteria(CredentialAccountUserCriteria criteria);
}
