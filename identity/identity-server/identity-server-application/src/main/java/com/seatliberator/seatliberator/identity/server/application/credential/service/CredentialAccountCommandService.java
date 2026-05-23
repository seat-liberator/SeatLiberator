package com.seatliberator.seatliberator.identity.server.application.credential.service;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.RegisterCredentialAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.UpdatePasswordUseCase;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.RegisterCredentialAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.UpdatePasswordCommand;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountUserCriteria;
import com.seatliberator.seatliberator.identity.server.application.role.contract.InitialRoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.contract.UserCreator;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CredentialAccountCommandService implements
        RegisterCredentialAccountUseCase,
        UpdatePasswordUseCase {

    private final CredentialAccountReader reader;
    private final CredentialAccountStore store;

    private final UserCreator userCreator;
    private final InitialRoleGrantor roleGrantor;
    private final NamespaceRoleSerializer formatter;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    public AuthenticatedResult register(RegisterCredentialAccountCommand command) {
        var email = command.email();
        var criteria = CredentialAccountEmailCriteria.of(email);
        boolean existsAccount = reader.existsByCriteria(criteria);
        if (existsAccount) throw new IdentityApplicationException(IdentityApplicationErrorCode.EMAIL_DUPLICATED);

        var user = userCreator.create(command.nickname());

        var passwordHash = passwordEncoder.encode(command.password());

        var now = clock.instant();
        var account = CredentialAccount.of(user.getId(), command.email(), passwordHash, now);
        store.save(account);

        var userId = user.getId();
        var grants = roleGrantor.grantInitial(userId).stream()
                .map(grant -> formatter.serialize(grant.getNamespaceRole()))
                .collect(Collectors.toUnmodifiableSet());
        return AuthenticatedResult.from(userId, user.getNickname(), grants);
    }

    @Override
    public void update(UpdatePasswordCommand command) {
        var userId = command.userId();
        var criteria = CredentialAccountUserCriteria.of(userId);
        var account = reader.findByCriteria(criteria)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND));

        var matches = passwordEncoder.matches(command.oldPassword(), account.getPasswordHash());
        if (!matches) throw new IdentityApplicationException(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        var now = clock.instant();
        account.updatePasswordHash(command.newPassword(), now);
        store.save(account);
    }
}
