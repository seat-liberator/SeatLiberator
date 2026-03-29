package com.seatliberator.seatliberator.board.api.authorization.registration;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.role.api.DefaultNamespaceRoleGrant;
import com.seatliberator.seatliberator.role.api.DefaultNamespaceRoleGrantProvider;

import java.util.List;

public class BoardDefaultNamespaceRoleGrantProvider implements DefaultNamespaceRoleGrantProvider {
    @Override
    public List<DefaultNamespaceRoleGrant> grants() {
        return List.of(new DefaultNamespaceRoleGrant("board", Role.USER));
    }
}
