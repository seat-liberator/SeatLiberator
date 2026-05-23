package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.handler;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
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
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CredentialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenResponseProcessor tokenResponseProcessor;

    @Override
    public void onAuthenticationSuccess(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable Authentication authentication
    ) throws IOException, ServletException {
        if (authentication == null) return;
        if (!(authentication.getPrincipal() instanceof AuthenticatedResult(
                UUID userId, String nickname, Set<String> scopes
        ))) return;

        var principal = TrustedPrincipal.of(userId, nickname, scopes);

        tokenResponseProcessor.process(response, principal);
    }
}
