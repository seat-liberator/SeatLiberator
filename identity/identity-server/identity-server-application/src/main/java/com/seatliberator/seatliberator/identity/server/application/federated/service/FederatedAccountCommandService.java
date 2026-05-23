package com.seatliberator.seatliberator.identity.server.application.federated.service;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.LinkFederatedAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.RegisterFederatedAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.UnlinkFederatedAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.LinkFederatedAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.RegisterFederatedAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.UnlinkFederatedAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountUserRegistrationLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.role.contract.InitialRoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.contract.UserCreator;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FederatedAccountCommandService implements
        RegisterFederatedAccountUseCase,
        LinkFederatedAccountUseCase,
        UnlinkFederatedAccountUseCase {

    private final FederatedAccountReader reader;
    private final FederatedAccountStore store;

    private final UserReader userReader;
    private final UserCreator userCreator;
    private final InitialRoleGrantor roleGrantor;
    private final NamespaceRoleSerializer formatter;
    private final Clock clock;

    @Override
    public AuthenticatedResult register(RegisterFederatedAccountCommand command) {
        var registrationId = command.registrationId();
        var providerUserId = command.providerUserId();
        ensureNotExistsAccount(registrationId, providerUserId);

        var now = clock.instant();
        var user = userCreator.create(command.providerUserNickname());
        var account = FederatedAccount.of(user.getId(), registrationId, providerUserId, now);
        store.save(account);

        var userId = user.getId();
        var grants = roleGrantor.grantInitial(userId).stream()
                .map(grant -> formatter.serialize(grant.getNamespaceRole()))
                .collect(Collectors.toUnmodifiableSet());
        return AuthenticatedResult.from(userId, user.getNickname(), grants);

    }

    @Override
    public void link(LinkFederatedAccountCommand command) {
        var registrationId = command.registrationId();
        var providerUserId = command.providerUserId();
        ensureNotExistsAccount(registrationId, providerUserId);

        var userId = command.userId();
        var existsUser = userReader.existsById(userId);
        if (!existsUser) throw new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND);

        var now = clock.instant();
        var account = FederatedAccount.of(userId, registrationId, providerUserId, now);
        store.save(account);
    }

    @Override
    public void unlink(UnlinkFederatedAccountCommand command) {
        var criteria = FederatedAccountUserRegistrationLookupCriteria.of(command.userId(), command.registrationId());
        var account = reader.findByCriteria(criteria)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND));

        store.delete(account);
    }

    private void ensureNotExistsAccount(String registrationId, String providerUserId) {
        var criteria = FederatedAccountLookupCriteria.of(registrationId, providerUserId);
        var existsAccount = reader.existsByCriteria(criteria);
        if (existsAccount) throw new IdentityApplicationException(IdentityApplicationErrorCode.ACCOUNT_ALREADY_EXISTS);
    }
}
