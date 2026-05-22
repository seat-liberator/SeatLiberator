package com.seatliberator.seatliberator.identity.server.persistence.credential.jpa;

import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountUserCriteria;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import com.seatliberator.seatliberator.identity.server.persistence.credential.jpa.repository.CredentialAccountRepository;
import com.seatliberator.seatliberator.identity.server.persistence.shared.jpa.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCredentialAccountPersistenceAdapter implements CredentialAccountReader, CredentialAccountStore {
    private final CredentialAccountRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByCriteria(CredentialAccountEmailCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public Optional<CredentialAccount> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<CredentialAccount> findByCriteria(CredentialAccountEmailCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public Optional<CredentialAccount> findByCriteria(CredentialAccountUserCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public CredentialAccount save(CredentialAccount credentialAccount) {
        return repository.save(credentialAccount);
    }

    @Override
    public void delete(CredentialAccount credentialAccount) {
        repository.delete(credentialAccount);
    }

    private Specification<CredentialAccount> createSpecificationFromCriteria(CredentialAccountEmailCriteria criteria) {
        var spec = Specification.<CredentialAccount>unrestricted();
        spec = spec.and(CommonPredicates.equals(criteria.email(), from -> from.get("email")));

        return spec;
    }

    private Specification<CredentialAccount> createSpecificationFromCriteria(CredentialAccountUserCriteria criteria) {
        var spec = Specification.<CredentialAccount>unrestricted();

        spec = spec.and(CommonPredicates.equals(criteria.userId(), from -> from.get("userId")));

        return spec;
    }
}
