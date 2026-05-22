package com.seatliberator.seatliberator.identity.server.application.role.port.out;

import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;

import java.util.List;

public interface UserGrantedRoleStore {
    UserGrantedRole save(UserGrantedRole userGrantedRole);

    List<UserGrantedRole> saveAll(List<UserGrantedRole> userGrantedRoles);

    List<UserGrantedRole> findByUserId(String userId);

    void deleteByUserIdAndNamespace(String userId, String namespace);
}
