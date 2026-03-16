package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.FederatedPrincipal;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler.TokenIssueProcessor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
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
        log.debug("Handling federated authentication success response.");

        if (authentication == null) {
            log.debug("Federated authentication success handling skipped because authentication was null.");
            return;
        }

        var principal = authentication.getPrincipal();

        if (principal == null) {
            log.debug("Federated authentication success handling skipped because principal was null.");
            return;
        }

        if (!(principal instanceof FederatedPrincipal federatedPrincipal)) {
            log.debug(
                    "Federated authentication success handling skipped because principal was not federated principal. principalType={}",
                    principal.getClass().getName()
            );
            return;
        }

        log.debug(
                "Federated principal resolved for success handling. registrationId={}, email={}",
                federatedPrincipal.registrationId(),
                federatedPrincipal.email()
        );

        var actor = federatedSignInProcessor.authenticate(federatedPrincipal);

        log.debug(
                "Federated sign-in processor succeeded. registrationId={}, subject={}",
                federatedPrincipal.registrationId(),
                actor.subject()
        );

        var issuedTokenEntry = tokenIssueProcessor.process(actor.subject());

        log.debug(
                "Token issuance succeeded for federated authentication. subject={}",
                actor.subject()
        );

        responseWriter.write(
                response,
                HttpStatus.OK,
                issuedTokenEntry
        );

        log.debug(
                "Federated authentication success response written. subject={}",
                actor.subject()
        );
    }
}
