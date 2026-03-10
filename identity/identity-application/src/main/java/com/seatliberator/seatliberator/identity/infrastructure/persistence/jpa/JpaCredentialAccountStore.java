package com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.identity.application.port.out.AuthContextRecord;
import com.seatliberator.seatliberator.identity.application.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.domain.CredentialAccount;
import com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa.repository.CredentialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCredentialAccountStore implements CredentialAccountStore {
    private final CredentialAccountRepository credentialAccountRepository;

    @Override
    public Optional<CredentialAccount> findByEmail(String email) {
        return credentialAccountRepository.findByEmail(email);
    }

    @Override
    public Optional<AuthContextRecord.Credential> findCredentialAuthByEmail(String email) {
        return credentialAccountRepository.findAuthByEmail(email)
                .map(credentialAccount -> new AuthContextRecord.Credential(
                        credentialAccount.getUser().getId(),
                        credentialAccount.getEmail(),
                        credentialAccount.getPasswordHash()
                ));
    }

    @Override
    public boolean existsByEmail(String email) {
        return credentialAccountRepository.existsByEmail(email);
    }

    @Override
    public CredentialAccount save(CredentialAccount credentialAccount) {
        return credentialAccountRepository.save(credentialAccount);
    }
}
