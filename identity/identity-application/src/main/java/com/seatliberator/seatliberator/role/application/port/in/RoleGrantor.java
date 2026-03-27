package com.seatliberator.seatliberator.role.application.port.in;

import com.seatliberator.seatliberator.identity.core.role.Role;

public interface RoleGrantor {
    UserGrantedRoleEntry grant(String userId, String namespace, Role role);
}
