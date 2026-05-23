package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationFederatedUseCase;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationFederatedCommand;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.FederatedPrincipal;
import com.seatliberator.seatliberator.identity.server.security.shared.principal.TrustedPrincipal;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenResponseProcessor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class FederatedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthenticationFederatedUseCase useCase;
    private final TokenResponseProcessor tokenResponseProcessor;

    @Override
    public void onAuthenticationSuccess(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable Authentication authentication
    ) throws IOException, ServletException {
        if (authentication == null) return;
        if (!(authentication.getPrincipal() instanceof FederatedPrincipal principal)) return;

        var command = AuthenticationFederatedCommand.of(principal.registrationId(), principal.providerUserId());
        var auth = useCase.authenticate(command);

        var trustedPrincipal = TrustedPrincipal.of(auth.userId(), auth.nickname(), auth.scopes());
        tokenResponseProcessor.process(response, trustedPrincipal);
    }
}
