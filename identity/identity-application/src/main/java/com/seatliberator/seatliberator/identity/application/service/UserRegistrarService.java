package com.seatliberator.seatliberator.identity.application.service;

import com.seatliberator.seatliberator.identity.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.identity.application.exception.ApplicationException;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.application.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.application.port.out.UserStore;
import com.seatliberator.seatliberator.identity.domain.CredentialAccount;
import com.seatliberator.seatliberator.identity.domain.FederatedAccount;
import com.seatliberator.seatliberator.identity.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrarService implements UserRegistrar {
    private final CredentialAccountStore credentialAccountStore;
    private final FederatedAccountStore federatedAccountStore;
    private final UserStore userStore;

    private final PasswordEncoder passwordEncoder;

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
            throw new ApplicationException(ApplicationErrorCode.EMAIL_DUPLICATED);
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

        return new AuthEntry(
                savedUser.getId(),
                savedUser.getNickname()
        );
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
            throw new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED);
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

        return new AuthEntry(
                savedUser.getId(),
                savedUser.getNickname()
        );
    }
}
