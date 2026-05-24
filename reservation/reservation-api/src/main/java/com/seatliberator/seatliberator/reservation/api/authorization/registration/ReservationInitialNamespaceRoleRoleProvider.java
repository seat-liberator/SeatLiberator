package com.seatliberator.seatliberator.reservation.api.authorization.registration;

import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleProvider;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.reservation.api.ReservationApi;

public class ReservationInitialNamespaceRoleRoleProvider implements InitialNamespaceRoleProvider {
    @Override
    public NamespaceRole provide() {
        return SimpleNamespaceRole.from(ReservationApi.NAMESPACE, Role.USER);
    }
}
