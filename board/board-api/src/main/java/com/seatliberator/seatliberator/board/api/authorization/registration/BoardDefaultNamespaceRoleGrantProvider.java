package com.seatliberator.seatliberator.board.api.authorization.registration;

import com.seatliberator.seatliberator.board.api.BoardApi;
import com.seatliberator.seatliberator.identity.api.DefaultNamespaceRoleGrant;
import com.seatliberator.seatliberator.identity.api.DefaultNamespaceRoleGrantProvider;
import com.seatliberator.seatliberator.identity.core.role.Role;

import java.util.List;

public class BoardDefaultNamespaceRoleGrantProvider implements DefaultNamespaceRoleGrantProvider {
    @Override
    public List<DefaultNamespaceRoleGrant> grants() {
        return List.of(new DefaultNamespaceRoleGrant(BoardApi.NAMESPACE, Role.USER));
    }
}
