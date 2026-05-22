package com.seatliberator.seatliberator.identity.server.persistence.federated.jpa;

import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountUserRegistrationLookupCriteria;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import com.seatliberator.seatliberator.identity.server.persistence.federated.jpa.repository.FederatedAccountRepository;
import com.seatliberator.seatliberator.identity.server.persistence.shared.jpa.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaFederatedAccountPersistenceAdapter implements FederatedAccountReader, FederatedAccountStore {
    private final FederatedAccountRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByCriteria(FederatedAccountLookupCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public Optional<FederatedAccount> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<FederatedAccount> findByCriteria(FederatedAccountLookupCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public Optional<FederatedAccount> findByCriteria(FederatedAccountUserRegistrationLookupCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public FederatedAccount save(FederatedAccount federatedAccount) {
        return repository.save(federatedAccount);
    }

    @Override
    public void delete(FederatedAccount federatedAccount) {
        repository.delete(federatedAccount);
    }

    private Specification<FederatedAccount> createSpecificationFromCriteria(FederatedAccountLookupCriteria criteria) {
        var spec = Specification.<FederatedAccount>unrestricted();

        spec = spec.and(CommonPredicates.equals(criteria.registrationId(), from -> from.get("registrationId")));
        spec = spec.and(CommonPredicates.equals(criteria.providerUserId(), from -> from.get("providerUserId")));

        return spec;
    }

    private Specification<FederatedAccount> createSpecificationFromCriteria(FederatedAccountUserRegistrationLookupCriteria criteria) {
        var spec = Specification.<FederatedAccount>unrestricted();

        spec = spec.and(CommonPredicates.equals(criteria.userId(), from -> from.get("userId")));
        spec = spec.and(CommonPredicates.equals(criteria.registrationId(), from -> from.get("registrationId")));

        return spec;
    }
}
