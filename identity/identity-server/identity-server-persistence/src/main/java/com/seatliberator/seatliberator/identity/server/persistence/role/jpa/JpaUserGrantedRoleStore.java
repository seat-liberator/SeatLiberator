package com.seatliberator.seatliberator.identity.server.persistence.role.jpa;

import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.criteria.UserGrantedRoleUserNamespaceCriteria;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import com.seatliberator.seatliberator.identity.server.persistence.role.jpa.repository.UserGrantedRoleRepository;
import com.seatliberator.seatliberator.identity.server.persistence.shared.jpa.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserGrantedRoleStore implements UserGrantedRoleReader, UserGrantedRoleStore {
    private final UserGrantedRoleRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<UserGrantedRole> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<UserGrantedRole> findByCriteria(UserGrantedRoleUserNamespaceCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public List<UserGrantedRole> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public UserGrantedRole save(UserGrantedRole grantedRole) {
        return repository.save(grantedRole);
    }

    @Override
    public List<UserGrantedRole> saveAll(Collection<UserGrantedRole> grantedRoles) {
        return repository.saveAll(grantedRoles);
    }

    @Override
    public void delete(UserGrantedRole grantedRole) {
        repository.delete(grantedRole);
    }

    private Specification<UserGrantedRole> createSpecificationFromCriteria(UserGrantedRoleUserNamespaceCriteria criteria) {
        var spec = Specification.<UserGrantedRole>unrestricted();

        var namespace = criteria.namespace().value();
        spec = spec.and(CommonPredicates.equals(criteria.userId(), from -> from.get("userId")));
        spec = spec.and(CommonPredicates.equals(namespace, from -> from.get("namespaceRole").get("namespace")));

        return spec;
    }
}
