package com.seatliberator.seatliberator.identity.server.persistence.role.jpa.repository;

import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface UserGrantedRoleRepository extends JpaRepository<UserGrantedRole, UUID>, JpaSpecificationExecutor<UserGrantedRole> {
    List<UserGrantedRole> findByUserId(UUID userId);
}
