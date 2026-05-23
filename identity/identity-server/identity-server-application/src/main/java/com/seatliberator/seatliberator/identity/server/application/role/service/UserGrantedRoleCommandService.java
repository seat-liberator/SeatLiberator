package com.seatliberator.seatliberator.identity.server.application.role.service;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.GrantRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.RevokeRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.UpdateRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.GrantRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.RevokeRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.UpdateRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedRoleResult;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.criteria.UserGrantedRoleUserNamespaceCriteria;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGrantedRoleCommandService implements
        GrantRoleUseCase,
        UpdateRoleUseCase,
        RevokeRoleUseCase {

    private final UserGrantedRoleReader reader;
    private final UserGrantedRoleStore store;

    private final UserReader userReader;
    private final Clock clock;

    @Override
    public UserGrantedRoleResult grant(GrantRoleCommand command) {
        var userId = command.userId();
        var existsUser = userReader.existsById(userId);
        if (!existsUser) throw new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND);

        var now = clock.instant();
        var grantedRole = UserGrantedRole.of(userId, command.namespaceRole(), now);

        var saved = store.save(grantedRole);

        return UserGrantedRoleResult.from(saved);
    }

    @Override
    public UserGrantedRoleResult update(UpdateRoleCommand command) {
        var userId = command.userId();
        var existsUser = userReader.existsById(userId);
        if (!existsUser) throw new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND);

        var criteria = UserGrantedRoleUserNamespaceCriteria.of(userId, command.namespace());
        var grantedRole = reader.findByCriteria(criteria)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.GRANT_NOT_FOUND));

        grantedRole.updateRole(command.role());

        var saved = store.save(grantedRole);

        return UserGrantedRoleResult.from(saved);
    }

    @Override
    public void revoke(RevokeRoleCommand command) {
        var userId = command.userId();
        var existsUser = userReader.existsById(userId);
        if (!existsUser) throw new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND);

        var namespace = command.namespace();
        var criteria = UserGrantedRoleUserNamespaceCriteria.of(userId, namespace);
        var grantedRole = reader.findByCriteria(criteria)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.GRANT_NOT_FOUND));

        store.delete(grantedRole);
    }
}
