package com.seatliberator.seatliberator.identity.application.port.out;

import com.seatliberator.seatliberator.identity.domain.CredentialAccount;

import java.util.Optional;

public interface CredentialAccountStore {
    Optional<CredentialAccount> findByEmail(String email);

    Optional<AuthContextRecord.Credential> findCredentialAuthByEmail(String email);

    boolean existsByEmail(String email);

    CredentialAccount save(CredentialAccount credentialAccount);
}
