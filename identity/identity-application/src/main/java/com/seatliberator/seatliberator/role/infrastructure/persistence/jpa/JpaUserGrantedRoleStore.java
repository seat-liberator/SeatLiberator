package com.seatliberator.seatliberator.role.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.role.application.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.role.domain.UserGrantedRole;
import com.seatliberator.seatliberator.role.infrastructure.persistence.jpa.repository.UserGrantedRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaUserGrantedRoleStore implements UserGrantedRoleStore {
    private final UserGrantedRoleRepository repository;

    @Override
    public UserGrantedRole save(UserGrantedRole userGrantedRole) {
        return repository.save(userGrantedRole);
    }

    @Override
    public List<UserGrantedRole> saveAll(List<UserGrantedRole> userGrantedRoles) {
        return repository.saveAll(userGrantedRoles);
    }

    @Override
    public List<UserGrantedRole> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndNamespace(String userId, String namespace) {
        repository.deleteByUserIdAndNamespace(userId, namespace);
    }
}
