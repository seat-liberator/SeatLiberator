package com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.identity.application.port.out.AuthRecord;
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
    public Optional<AuthRecord.Credential> findCredentialAuthByEmail(String email) {
        return credentialAccountRepository.findAuthByEmail(email)
                .map(credentialAccount -> new AuthRecord.Credential(
                        credentialAccount.getUser().getId(),
                        credentialAccount.getUser().getNickname(),
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
