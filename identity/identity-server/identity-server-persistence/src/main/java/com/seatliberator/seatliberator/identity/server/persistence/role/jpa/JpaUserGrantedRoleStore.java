package com.seatliberator.seatliberator.identity.server.persistence.role.jpa;

import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import com.seatliberator.seatliberator.identity.server.persistence.role.jpa.repository.UserGrantedRoleRepository;
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
