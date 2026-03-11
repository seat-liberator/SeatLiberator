package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CredentialAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ResponseWriter responseWriter;

    @Override
    public void onAuthenticationFailure(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        log.debug(
                "Credential authentication failed. message={}",
                exception.getMessage()
        );

        responseWriter.write(
                response,
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );

        log.debug("Credential authentication failure response written.");
    }
}
