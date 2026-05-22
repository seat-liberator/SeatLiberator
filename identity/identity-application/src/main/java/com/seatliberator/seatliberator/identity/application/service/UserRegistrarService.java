package com.seatliberator.seatliberator.identity.application.service;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.identity.api.event.IdentityEventType;
import com.seatliberator.seatliberator.identity.api.event.payload.UserRegisteredEventPayload;
import com.seatliberator.seatliberator.identity.application.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.application.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.application.factory.AuthEntryFactory;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.application.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.UserStore;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.role.application.port.in.RoleGrantor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrarService implements UserRegistrar {
    private final CredentialAccountStore credentialAccountStore;
    private final FederatedAccountStore federatedAccountStore;
    private final UserStore userStore;

    private final BootstrapDefaultGrantRegistry bootstrapDefaultGrantRegistry;
    private final RoleGrantor roleGrantor;
    private final AuthEntryFactory authEntryFactory;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final EventTraceHolder eventTraceHolder;
    private final Clock clock;

    @Override
    public AuthEntry register(RegistrationCommand.Credential command) {
        log.debug(
                "Attempting credential registration. email={}, nickname={}",
                command.email(),
                command.nickname()
        );

        boolean existsAccount = credentialAccountStore.existsByEmail(command.email());

        if (existsAccount) {
            log.debug(
                    "Credential registration failed because email already exists. email={}",
                    command.email()
            );
            throw new IdentityApplicationException(IdentityApplicationErrorCode.EMAIL_DUPLICATED);
        }

        var passwordHash = passwordEncoder.encode(command.password());
        log.debug(
                "Password encoded for credential registration. email={}",
                command.email()
        );

        var user = User.create(command.nickname());
        log.debug(
                "User entity created for credential registration. nickname={}",
                command.nickname()
        );

        var account = CredentialAccount.create(
                command.email(),
                passwordHash
        );
        log.debug(
                "Credential account entity created. email={}",
                command.email()
        );

        user.setCredentialAccount(account);
        log.debug(
                "Credential account linked to user. email={}, nickname={}",
                command.email(),
                command.nickname()
        );

        var savedUser = userStore.save(user);

        log.debug(
                "Credential registration succeeded. email={}, userId={}, nickname={}",
                command.email(),
                savedUser.getId(),
                savedUser.getNickname()
        );


        var defaultRoles = bootstrapDefaultGrantRegistry.getDefaultNamespaceRole();
        roleGrantor.grantAll(savedUser.getId().toString(), defaultRoles);

        eventTraceHolder.with(
                () -> eventPublisher.publish(IdentityEventType.USER_REGISTERED, new UserRegisteredEventPayload(savedUser.getId().toString(), clock.instant())),
                state -> state
                        .withAggregate("user", savedUser.getId().toString())
        );

        return authEntryFactory.create(savedUser.getId(), savedUser.getNickname());
    }

    @Override
    public AuthEntry register(RegistrationCommand.Federated command) {
        log.debug(
                "Attempting federated registration. registrationId={}, nickname={}",
                command.registrationId(),
                command.nickname()
        );

        boolean existsAccount = federatedAccountStore.existsByRegistrationIdAndProviderUserId(
                command.registrationId(),
                command.providerUserId()
        );

        if (existsAccount) {
            log.debug(
                    "Federated registration failed because account already exists. registrationId={}",
                    command.registrationId()
            );
            throw new IdentityApplicationException(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);
        }

        log.debug(
                "Federated registration account is available. registrationId={}",
                command.registrationId()
        );

        var user = User.create(command.nickname());
        log.debug(
                "User entity created for federated registration. registrationId={}, nickname={}",
                command.registrationId(),
                command.nickname()
        );

        var account = FederatedAccount.create(
                command.registrationId(),
                command.providerUserId()
        );
        log.debug(
                "Federated account entity created. registrationId={}",
                command.registrationId()
        );

        user.addFederatedAccount(account);
        log.debug(
                "Federated account linked to user. registrationId={}, nickname={}",
                command.registrationId(),
                command.nickname()
        );

        var savedUser = userStore.save(user);

        log.debug(
                "Federated registration succeeded. registrationId={}, userId={}, nickname={}",
                command.registrationId(),
                savedUser.getId(),
                savedUser.getNickname()
        );

        var defaultRoles = bootstrapDefaultGrantRegistry.getDefaultNamespaceRole();
        roleGrantor.grantAll(savedUser.getId().toString(), defaultRoles);

        return authEntryFactory.create(savedUser.getId(), savedUser.getNickname());
    }
}
