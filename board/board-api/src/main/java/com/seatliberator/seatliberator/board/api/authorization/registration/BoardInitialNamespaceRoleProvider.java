package com.seatliberator.seatliberator.board.api.authorization.registration;

import com.seatliberator.seatliberator.board.api.BoardApi;
import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleProvider;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;

public class BoardInitialNamespaceRoleProvider implements InitialNamespaceRoleProvider {
    @Override
    public NamespaceRole provide() {
        return SimpleNamespaceRole.from(BoardApi.NAMESPACE, Role.USER);
    }
}
