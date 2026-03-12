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
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class DefaultFederatedSignInProcessor implements FederatedSignInProcessor {
    private final AccountAuthenticator accountAuthenticator;
    private final AccountExistenceChecker accountExistenceChecker;
    private final UserRegistrar userRegistrar;


    @Override
    public Actor authenticate(FederatedPrincipal principal) {
        log.debug(
                "Attempting federated sign-in processing. registrationId={}, email={}",
                principal.registrationId(),
                principal.email()
        );

        var existsCommand = new ExistenceCheckingCommand.Federated(
                principal.registrationId(),
                principal.providerUserId()
        );

        boolean existsAccount = accountExistenceChecker.isFederatedAccountExists(
                existsCommand
        );

        log.debug(
                "Federated account existence check completed. registrationId={}, exists={}",
                principal.registrationId(),
                existsAccount
        );

        if (!existsAccount) {
            log.debug(
                    "Federated account not found. proceeding with federated registration. registrationId={}, nickname={}",
                    principal.registrationId(),
                    principal.nickname()
            );

            var registrationCommand = new RegistrationCommand.Federated(
                    principal.nickname(),
                    principal.registrationId(),
                    principal.providerUserId()
            );

            userRegistrar.register(registrationCommand);

            log.debug(
                    "Federated registration succeeded during sign-in processing. registrationId={}",
                    principal.registrationId()
            );
        }

        var authenticationCommand = new AuthenticationCommand.Federated(
                principal.registrationId(),
                principal.providerUserId()
        );

        log.debug(
                "Federated authentication command created during sign-in processing. registrationId={}",
                principal.registrationId()
        );

        var authContext = accountAuthenticator.authenticate(authenticationCommand);

        log.debug(
                "Federated authentication succeeded during sign-in processing. registrationId={}, userId={}",
                principal.registrationId(),
                authContext.userId()
        );

        var actor = new SimpleActor(
                authContext.userId().toString(),
                Set.of("ROLE_USER")
        );

        log.debug(
                "Federated sign-in processing completed. registrationId={}, subject={}",
                principal.registrationId(),
                actor.subject()
        );

        return actor;
    }
}
