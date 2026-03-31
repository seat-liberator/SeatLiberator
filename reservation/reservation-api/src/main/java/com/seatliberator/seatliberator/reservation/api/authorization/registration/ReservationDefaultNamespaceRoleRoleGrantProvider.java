package com.seatliberator.seatliberator.reservation.api.authorization.registration;

import com.seatliberator.seatliberator.identity.api.DefaultNamespaceRoleGrant;
import com.seatliberator.seatliberator.identity.api.DefaultNamespaceRoleGrantProvider;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.reservation.api.ReservationApi;

import java.util.List;

public class ReservationDefaultNamespaceRoleRoleGrantProvider implements DefaultNamespaceRoleGrantProvider {

    @Override
    public List<DefaultNamespaceRoleGrant> grants() {
        return List.of(new DefaultNamespaceRoleGrant(ReservationApi.NAMESPACE, Role.USER));
    }
}
