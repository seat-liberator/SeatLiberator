package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.IssuedTokenEntry;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler.TokenIssueProcessor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class FederatedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final FederatedSignInProcessor federatedSignInProcessor;
    private final TokenIssueProcessor tokenIssueProcessor;
    private final ResponseWriter responseWriter;

    @Override
    public void onAuthenticationSuccess(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable Authentication authentication
    ) throws IOException, ServletException {
        if (authentication == null || authentication.getPrincipal() == null) return;
        if (!(authentication.getPrincipal() instanceof FederatedPrincipal principal)) return;

        Actor actor = federatedSignInProcessor.authenticate(principal);

        IssuedTokenEntry issuedTokenEntry = tokenIssueProcessor.process(actor.subject());

        responseWriter.write(
                response,
                HttpStatus.OK,
                issuedTokenEntry
        );
    }
}
