package com.seatliberator.seatliberator.identity.server.application.role.contract;

import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleRegistry;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitialRoleGrantor {
    private final UserGrantedRoleStore store;

    private final InitialNamespaceRoleRegistry initialNamespaceRoleRegistry;
    private final UserReader userReader;
    private final Clock clock;

    public List<UserGrantedRole> grantInitial(UUID userId) {
        var now = clock.instant();
        var grants = initialNamespaceRoleRegistry.getAll().stream()
                .map(nr -> UserGrantedRole.of(userId, nr, now))
                .toList();

        return store.saveAll(grants);
    }
}
