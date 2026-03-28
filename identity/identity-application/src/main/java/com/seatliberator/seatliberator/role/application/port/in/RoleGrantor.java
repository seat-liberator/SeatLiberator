package com.seatliberator.seatliberator.role.application.port.in;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;

import java.util.List;

public interface RoleGrantor {
    UserGrantedRoleEntry grant(String userId, NamespaceRole namespaceRole);

    List<UserGrantedRoleEntry> grantAll(String userId, List<NamespaceRole> namespaceRoles);
}
