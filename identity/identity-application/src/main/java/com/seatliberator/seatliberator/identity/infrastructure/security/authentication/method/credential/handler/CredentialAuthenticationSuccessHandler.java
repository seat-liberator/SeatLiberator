package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedUserPrincipal;
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
public class CredentialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenIssueProcessor tokenIssueProcessor;
    private final ResponseWriter responseWriter;

    @Override
    public void onAuthenticationSuccess(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable Authentication authentication
    ) throws IOException, ServletException {
        log.debug("Handling credential authentication success response.");

        if (authentication == null) {
            log.debug("Credential authentication success handling skipped because authentication was null.");
            return;
        }

        if (!(authentication instanceof TrustedAuthenticationToken)) {
            log.debug(
                    "Credential authentication success handling skipped because authentication was not trusted. authenticationType={}",
                    authentication.getClass().getName()
            );
            return;
        }

        var principal = authentication.getPrincipal();

        if (!(principal instanceof TrustedUserPrincipal(String subject, String nickname))) {
            log.debug(
                    "Credential authentication success handling skipped because principal was not trusted user principal. principalType={}",
                    principal == null ? "null" : principal.getClass().getName()
            );
            return;
        }

        log.debug(
                "Trusted authentication resolved for credential success handling. subject={}, nickname={}",
                subject,
                nickname
        );

        var issuedTokenEntry = tokenIssueProcessor.process(subject);
        log.debug("Token issuance succeeded for credential authentication. subject={}", subject);

        responseWriter.write(
                response,
                HttpStatus.OK,
                issuedTokenEntry
        );

        log.debug("Credential authentication success response written. subject={}", subject);
    }
}
