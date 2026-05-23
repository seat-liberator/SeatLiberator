package com.seatliberator.seatliberator.identity.server.application.role.port.out;

import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;

import java.util.Collection;
import java.util.List;

public interface UserGrantedRoleStore {
    UserGrantedRole save(UserGrantedRole grantedRole);

    List<UserGrantedRole> saveAll(Collection<UserGrantedRole> grantedRoles);

    void delete(UserGrantedRole grantedRole);
}
