package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.server.application.shared.port.in.AccountAuthenticator;
import com.seatliberator.seatliberator.identity.server.application.shared.port.in.AccountExistenceChecker;
import com.seatliberator.seatliberator.identity.server.application.shared.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.server.application.shared.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.server.application.shared.port.in.command.ExistenceCheckingCommand;
import com.seatliberator.seatliberator.identity.server.application.shared.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.server.application.shared.port.in.result.AuthEntry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.FederatedPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultFederatedSignInProcessor implements FederatedSignInProcessor {
    private final AccountAuthenticator accountAuthenticator;
    private final AccountExistenceChecker accountExistenceChecker;
    private final UserRegistrar userRegistrar;

    @Override
    public AuthEntry process(FederatedPrincipal principal) {
        var nick = principal.nickname();
        var regId = principal.registrationId();
        var prvId = principal.providerUserId();

        var existsCommand = new ExistenceCheckingCommand.Federated(regId, prvId);
        boolean existsAccount = accountExistenceChecker.isFederatedAccountExists(existsCommand);

        if (!existsAccount) {
            var registrationCommand = new RegistrationCommand.Federated(nick, regId, prvId);
            return userRegistrar.register(registrationCommand);
        } else {
            var authenticationCommand = new AuthenticationCommand.Federated(regId, prvId);
            return accountAuthenticator.authenticate(authenticationCommand);
        }
    }
}
