package com.seatliberator.seatliberator.role.application.port.out;

import com.seatliberator.seatliberator.role.domain.UserGrantedRole;

import java.util.List;

public interface UserGrantedRoleStore {
    UserGrantedRole save(UserGrantedRole userGrantedRole);

    List<UserGrantedRole> findByUserId(String userId);

    void deleteByUserIdAndNamespace(String userId, String namespace);
}
