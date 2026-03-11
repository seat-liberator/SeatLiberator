package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.application.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.application.port.in.AccountExistenceChecker;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.command.ExistenceCheckingCommand;
import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class DefaultFederatedSignInProcessor implements FederatedSignInProcessor {
    private final AccountAuthenticator accountAuthenticator;
    private final AccountExistenceChecker accountExistenceChecker;
    private final UserRegistrar userRegistrar;


    @Override
    public Actor authenticate(FederatedPrincipal principal) {
        var existsCommand = new ExistenceCheckingCommand.Federated(
                principal.registrationId(),
                principal.providerUserId()
        );

        boolean existsAccount = accountExistenceChecker.isFederatedAccountExists(
                existsCommand
        );

        if (!existsAccount) {
            var registrationCommand = new RegistrationCommand.Federated(
                    principal.nickname(),
                    principal.registrationId(),
                    principal.providerUserId()
            );

            userRegistrar.register(registrationCommand);
        }

        var authenticationCommand = new AuthenticationCommand.Federated(
                principal.registrationId(),
                principal.providerUserId()
        );

        var authContext = accountAuthenticator.authenticate(authenticationCommand);

        return new SimpleActor(
                authContext.userId().toString(),
                Set.of("ROLE_USER")
        );
    }
}
