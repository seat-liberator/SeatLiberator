package com.seatliberator.seatliberator.role.application.service;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.role.application.port.in.RoleGrantor;
import com.seatliberator.seatliberator.role.application.port.in.RoleReader;
import com.seatliberator.seatliberator.role.application.port.in.RoleRevoker;
import com.seatliberator.seatliberator.role.application.port.in.UserGrantedRoleEntry;
import com.seatliberator.seatliberator.role.application.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.role.domain.UserGrantedRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGrantedRoleService implements RoleGrantor, RoleReader, RoleRevoker {
    private final UserGrantedRoleStore userGrantedRoleStore;

    @Override
    public UserGrantedRoleEntry grant(String userId, String namespace, Role role) {
        var grant = UserGrantedRole.from(userId, namespace, role);
        var saved = userGrantedRoleStore.save(grant);
        return UserGrantedRoleEntry.from(saved);
    }

    @Override
    public List<UserGrantedRoleEntry> read(String userId) {
        return userGrantedRoleStore.findByUserId(userId).stream().map(UserGrantedRoleEntry::from).toList();
    }

    @Override
    public void revoke(String userId, String namespace) {
        userGrantedRoleStore.deleteByUserIdAndNamespace(userId, namespace);
    }
}
