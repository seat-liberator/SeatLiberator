package com.seatliberator.seatliberator.identity.server.application.role.service;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.FindUserGrantedSummaryUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.query.FindUserGrantedSummaryQuery;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.NamespaceRoleResult;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedSummaryResult;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGrantedRoleQueryService implements FindUserGrantedSummaryUseCase {
    private final UserGrantedRoleReader reader;
    private final UserReader userReader;

    @Override
    public UserGrantedSummaryResult find(FindUserGrantedSummaryQuery query) {
        var userId = query.userId();
        var existsUser = userReader.existsById(userId);
        if (!existsUser) throw new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND);

        var result = reader.findByUserId(userId);
        var first = result.getFirst();
        var namespaceRoles = result.stream()
                .map(grant -> NamespaceRoleResult.from(grant.getNamespaceRole()))
                .toList();

        return UserGrantedSummaryResult.of(first.getUserId(), namespaceRoles);
    }
}
