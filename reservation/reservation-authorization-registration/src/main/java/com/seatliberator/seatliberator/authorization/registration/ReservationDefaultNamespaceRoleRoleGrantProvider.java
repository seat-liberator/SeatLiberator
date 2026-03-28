package com.seatliberator.seatliberator.authorization.registration;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.role.api.DefaultNamespaceRoleGrant;
import com.seatliberator.seatliberator.role.api.DefaultNamespaceRoleGrantProvider;

import java.util.List;

public class ReservationDefaultNamespaceRoleRoleGrantProvider implements DefaultNamespaceRoleGrantProvider {

    @Override
    public List<DefaultNamespaceRoleGrant> grants() {
        return List.of(new DefaultNamespaceRoleGrant("reservation", Role.USER));
    }
}
