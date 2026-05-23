package com.seatliberator.seatliberator.identity.server.application.role.port.out;

import com.seatliberator.seatliberator.identity.server.application.role.port.out.criteria.UserGrantedRoleUserNamespaceCriteria;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGrantedRoleReader {
    boolean existsById(UUID id);

    Optional<UserGrantedRole> findById(UUID id);

    Optional<UserGrantedRole> findByCriteria(UserGrantedRoleUserNamespaceCriteria criteria);

    List<UserGrantedRole> findByUserId(UUID userId);
}
