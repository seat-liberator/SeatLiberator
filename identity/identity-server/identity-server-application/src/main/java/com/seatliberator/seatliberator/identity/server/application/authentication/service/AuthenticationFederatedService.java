package com.seatliberator.seatliberator.identity.server.application.authentication.service;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationFederatedUseCase;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationFederatedCommand;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.ScopeReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationFederatedService implements AuthenticationFederatedUseCase {
    private final FederatedAccountReader accountReader;
    private final UserReader userReader;
    private final ScopeReader scopeReader;

    @Override
    public AuthenticatedResult authenticate(AuthenticationFederatedCommand command) {
        var registrationId = command.registrationId();
        var providerUserId = command.providerUserId();

        var criteria = FederatedAccountLookupCriteria.of(registrationId, providerUserId);
        var account = accountReader.findByCriteria(criteria)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND));

        var userId = account.getUserId();
        var user = userReader.findById(userId)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND));
        var scopes = scopeReader.readScopes(userId.toString());
        return AuthenticatedResult.from(user.getId(), user.getNickname(), scopes);
    }
}
