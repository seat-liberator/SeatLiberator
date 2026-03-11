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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrarService implements UserRegistrar {
    private final CredentialAccountStore credentialAccountStore;
    private final FederatedAccountStore federatedAccountStore;
    private final UserStore userStore;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthEntry register(RegistrationCommand.Credential command) {
        boolean existsAccount = credentialAccountStore.existsByEmail(command.email());

        if (existsAccount) throw new ApplicationException(ApplicationErrorCode.EMAIL_DUPLICATED);

        var passwordHash = passwordEncoder.encode(command.password());
        var user = User.create(command.nickname());
        var account = CredentialAccount.create(
                command.email(),
                passwordHash
        );

        user.setCredentialAccount(account);

        var savedUser = userStore.save(user);

        return new AuthEntry(
                savedUser.getId(),
                savedUser.getNickname()
        );
    }

    @Override
    public AuthEntry register(RegistrationCommand.Federated command) {
        boolean existsAccount = federatedAccountStore.existsByRegistrationIdAndProviderUserId(
                command.registrationId(),
                command.providerUserId()
        );

        if (existsAccount) throw new ApplicationException(ApplicationErrorCode.AUTHENTICATION_FAILED);

        var user = User.create(command.nickname());
        var account = FederatedAccount.create(
                command.registrationId(),
                command.providerUserId()
        );

        user.addFederatedAccount(account);

        var savedUser = userStore.save(user);

        return new AuthEntry(
                savedUser.getId(),
                savedUser.getNickname()
        );
    }
}
