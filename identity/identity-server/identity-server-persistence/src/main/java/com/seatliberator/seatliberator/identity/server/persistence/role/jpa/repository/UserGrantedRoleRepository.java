package com.seatliberator.seatliberator.identity.server.persistence.role.jpa.repository;

import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserGrantedRoleRepository extends JpaRepository<UserGrantedRole, UUID> {
    List<UserGrantedRole> findByUserId(String userId);

    void deleteByUserIdAndNamespace(String userId, String namespace);
}
